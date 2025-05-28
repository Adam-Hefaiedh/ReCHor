package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.*;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Classe permettant d'implémenter l'algorithme Connection Scan Algorithm (CSA) permettant de calculer
 * le profil de tous les voyages optimaux dans un réseau de transport public.
 * @param timeTable valeur contenant les données horaires à utiliser.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public record Router(TimeTable timeTable) {

    /**
     * Permet de calculer le profil de tous les voyages optimaux
     * permettant de se rendre de n'importe quelle gare du réseau à
     * une gare d'arrivée donnée, un jour donné.
     * @param date la date pour laquelle les voyages optimaux doivent être calculés.
     * @param arrStationId l'identité de la gare de destination.
     *
     * @return le profil de tous les voyages optimaux permettant de se rendre
     * de n'importe quelle gare du réseau à celle de destination, le jour donné
     */
    public Profile profile(LocalDate date, int arrStationId) {
        Profile.Builder profileBuilder = new Profile.Builder(timeTable, date, arrStationId);
        Connections connections = timeTable.connectionsFor(date);
        Transfers transfers = timeTable.transfers();
        int[] walkable = walkingChangesArray(arrStationId);

        for (int connId = 0; connId < connections.size(); connId++) {
            int arrStopId = timeTable.stationId(connections.arrStopId(connId));
            int depStopId = timeTable.stationId(connections.depStopId(connId));
            int depMins = connections.depMins(connId);
            int arrMins = connections.arrMins(connId);
            int tripId = connections.tripId(connId);
            int tripPos = connections.tripPos(connId);

            ParetoFront.Builder frontBuild = new ParetoFront.Builder();

            option1_walkToDestination(frontBuild, arrStopId, arrMins, walkable, connId);
            option2_continueInTrip(frontBuild, profileBuilder, tripId);
            option3_changeVehicle(frontBuild, profileBuilder, arrStopId, arrMins, connId);

            if (!frontBuild.isEmpty()) {
                updateTripFrontier(profileBuilder, frontBuild, tripId);
                updateStationFrontiers(profileBuilder, frontBuild, transfers, connections,
                        depStopId, depMins, connId, tripPos);
            }
        }
        return profileBuilder.build();
    }


    private void option1_walkToDestination(ParetoFront.Builder frontBuild, int arrStopId, int arrMins,
                                           int[] walkable, int connectionId) {
        int walkMinutes = walkable[arrStopId];
        if (walkMinutes != -1) {
            int payload = Bits32_24_8.pack(connectionId, 0);
            frontBuild.add(arrMins + walkMinutes, 0, payload);
        }
    }

    private void option2_continueInTrip(ParetoFront.Builder frontBuild, Profile.Builder profile, int tripId) {
        ParetoFront.Builder tripFront = profile.forTrip(tripId);
        if (tripFront != null) frontBuild.addAll(tripFront);
    }

    private void option3_changeVehicle(ParetoFront.Builder frontBuild, Profile.Builder profile, int arrStopId,
                                       int arrMins, int connectionId) {
        ParetoFront.Builder stationFront = profile.forStation(arrStopId);
        if (stationFront != null) {
            stationFront.forEach(tuple -> {
                if (PackedCriteria.depMins(tuple) >= arrMins) {
                    int payload = Bits32_24_8.pack(connectionId, 0);
                    long t = PackedCriteria.withoutDepMins(
                            PackedCriteria.withPayload(
                                    PackedCriteria.withAdditionalChange(tuple),
                                    payload)
                    );
                    frontBuild.add(t);
                }
            });
        }
    }

    private void updateTripFrontier(Profile.Builder profile, ParetoFront.Builder frontBuild, int tripId) {
        if (profile.forTrip(tripId) == null) profile.setForTrip(tripId, frontBuild);
        else profile.forTrip(tripId).addAll(frontBuild);
    }

    private void updateStationFrontiers(Profile.Builder profile, ParetoFront.Builder frontBuild,
                                        Transfers transfers, Connections connections,
                                        int depStationId, int depMins,
                                        int connectionId, int connTripPos) {

        ParetoFront.Builder stationFront = profile.forStation(depStationId);
        boolean dominates = stationFront != null && stationFront.fullyDominates(frontBuild, depMins);
        if (dominates) return;

        int packedRange = transfers.arrivingAt(depStationId);
        for (int i = PackedRange.startInclusive(packedRange); i < PackedRange.endExclusive(packedRange); i++) {
            int currentDep = depMins - transfers.minutes(i);
            int fromStation = transfers.depStationId(i);

            frontBuild.forEach(tuple -> {
                int payloadConnId = Bits32_24_8.unpack24(PackedCriteria.payload(tuple));
                int stepCount = connections.tripPos(payloadConnId) - connTripPos;
                int payload = Bits32_24_8.pack(connectionId, stepCount);
                long updated = PackedCriteria.withDepMins(
                        PackedCriteria.withPayload(tuple, payload),
                        currentDep
                );
                if (profile.forStation(fromStation) == null)
                    profile.setForStation(fromStation, new ParetoFront.Builder().add(updated));
                else
                    profile.forStation(fromStation).add(updated);
            });
        }
    }

    private int[] walkingChangesArray(int arrStationId) {
        int[] walkable = new int[timeTable.stations().size()];
        Arrays.fill(walkable, -1);

        Transfers transfers = timeTable.transfers();
        int range = transfers.arrivingAt(arrStationId);

        for (int i = PackedRange.startInclusive(range); i < PackedRange.endExclusive(range); i++) {
            walkable[transfers.depStationId(i)] = transfers.minutes(i);
        }
        return walkable;
    }
}