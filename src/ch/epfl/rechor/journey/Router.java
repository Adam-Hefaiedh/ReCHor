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

            ParetoFront.Builder f = new ParetoFront.Builder();

            option1_walkToDestination(f, arrStopId, arrMins, walkable, connId);
            option2_continueInTrip(f, profileBuilder, tripId);
            option3_changeVehicle(f, profileBuilder, arrStopId, arrMins, connId);

            if (!f.isEmpty()) {
                updateTripFrontier(profileBuilder, f, tripId);
                updateStationFrontiers(profileBuilder, f, transfers, connections,
                        depStopId, depMins, connId, tripPos);
            }
        }
        return profileBuilder.build();
    }


    private void option1_walkToDestination(ParetoFront.Builder f, int arrStopId, int arrMins,
                                           int[] walkable, int connectionId) {
        int walkMinutes = walkable[arrStopId];
        if (walkMinutes != -1) {
            int payload = Bits32_24_8.pack(connectionId, 0);
            f.add(arrMins + walkMinutes, 0, payload);
        }
    }

    private void option2_continueInTrip(ParetoFront.Builder f, Profile.Builder p, int tripId) {
        ParetoFront.Builder tripFront = p.forTrip(tripId);
        if (tripFront != null) f.addAll(tripFront);
    }

    private void option3_changeVehicle(ParetoFront.Builder f, Profile.Builder p, int arrStopId,
                                       int arrMins, int connectionId) {
        ParetoFront.Builder stationFront = p.forStation(arrStopId);
        if (stationFront != null) {
            stationFront.forEach(tuple -> {
                if (PackedCriteria.depMins(tuple) >= arrMins) {
                    int payload = Bits32_24_8.pack(connectionId, 0);
                    long t = PackedCriteria.withoutDepMins(
                            PackedCriteria.withPayload(
                                    PackedCriteria.withAdditionalChange(tuple),
                                    payload)
                    );
                    f.add(t);
                }
            });
        }
    }

    private void updateTripFrontier(Profile.Builder p, ParetoFront.Builder f, int tripId) {
        if (p.forTrip(tripId) == null) p.setForTrip(tripId, f);
        else p.forTrip(tripId).addAll(f);
    }

    private void updateStationFrontiers(Profile.Builder p, ParetoFront.Builder f,
                                        Transfers transfers, Connections connections,
                                        int depStationId, int depMins,
                                        int connectionId, int connTripPos) {

        ParetoFront.Builder stationFront = p.forStation(depStationId);
        boolean dominates = stationFront != null && stationFront.fullyDominates(f, depMins);
        if (dominates) return;

        int packedRange = transfers.arrivingAt(depStationId);
        for (int i = PackedRange.startInclusive(packedRange); i < PackedRange.endExclusive(packedRange); i++) {
            int d = depMins - transfers.minutes(i);
            int fromStation = transfers.depStationId(i);

            f.forEach(tuple -> {
                int payloadConnId = Bits32_24_8.unpack24(PackedCriteria.payload(tuple));
                int stepCount = connections.tripPos(payloadConnId) - connTripPos;
                int payload = Bits32_24_8.pack(connectionId, stepCount);
                long updated = PackedCriteria.withDepMins(
                        PackedCriteria.withPayload(tuple, payload),
                        d
                );
                if (p.forStation(fromStation) == null)
                    p.setForStation(fromStation, new ParetoFront.Builder().add(updated));
                else
                    p.forStation(fromStation).add(updated);
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