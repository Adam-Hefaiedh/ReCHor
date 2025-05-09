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
    public Profile profile(LocalDate date , int arrStationId){
        Profile.Builder profileBuilder = new Profile.Builder(timeTable, date, arrStationId);
        Connections connections = timeTable.connectionsFor(date);
        Transfers transfers = timeTable.transfers();
        int[] walkableChanges = walkingChangesArray(arrStationId);
        for (int connectionID = 0; connectionID < connections.size(); connectionID++) {
            ParetoFront.Builder paretoBuild = new ParetoFront.Builder();
            int arrStopId = timeTable.stationId(connections.arrStopId(connectionID));
            int depStationId = timeTable.stationId(connections.depStopId(connectionID));
            int connectionDepMins = connections.depMins(connectionID);
            int connectionArrMins = connections.arrMins(connectionID);
            int connectionTripPos = connections.tripPos(connectionID);

            int currentConnection = connectionID;

            if(walkableChanges[arrStopId] != -1) {
                int payload = Bits32_24_8.pack(connectionID, 0);
                paretoBuild.add( connectionArrMins + walkableChanges[arrStopId] ,0,payload);
            }

            int tripId = connections.tripId(connectionID);
            ParetoFront.Builder tripFront = profileBuilder.forTrip(tripId);
            if (tripFront != null) {
                paretoBuild.addAll(tripFront);
            }

            if (profileBuilder.forStation(arrStopId) != null) {
                ParetoFront.Builder arrivalFront = profileBuilder.forStation(arrStopId);

                arrivalFront.forEach(f -> {
                    int depMins = PackedCriteria.depMins(f);
                    if (depMins >= connections.arrMins(currentConnection)) {
                        int updatedPayload = Bits32_24_8.pack(currentConnection,0);
                        long tuple = PackedCriteria.withoutDepMins(
                                PackedCriteria.withPayload(
                                        PackedCriteria.withAdditionalChange(f),
                                        updatedPayload)
                        );
                        paretoBuild.add(tuple);
                    }
                });
            }
            if(!paretoBuild.isEmpty())
            {
                if(profileBuilder.forTrip(tripId) == null)profileBuilder.setForTrip(tripId, paretoBuild);
                else profileBuilder.forTrip(tripId).addAll(paretoBuild);
                int inter = transfers.arrivingAt(depStationId);
                if(profileBuilder.forStation(depStationId) == null || !(profileBuilder.forStation(depStationId).fullyDominates(paretoBuild, connectionDepMins))) {
                    for (int j = PackedRange.startInclusive(inter); j < PackedRange.endExclusive(inter); j++) {
                        int d = connectionDepMins - transfers.minutes(j);
                        int currentStation = transfers.depStationId(j);
                        paretoBuild.forEach((t) -> {
                            int intermediatesStop = connections.tripPos(Bits32_24_8.unpack24(PackedCriteria.payload(t))) -
                                    connectionTripPos;
                            int payload = Bits32_24_8.pack(currentConnection, intermediatesStop);
                            long updatesTuple = PackedCriteria.withDepMins(
                                    PackedCriteria.withPayload(t,
                                            payload), d);

                            if ( profileBuilder.forStation(currentStation) == null)
                                profileBuilder.setForStation(currentStation, new ParetoFront.Builder().add(updatesTuple));
                            else
                                profileBuilder.forStation(currentStation).add(updatesTuple);
                        });
                    }
                }
            }
        }
        return profileBuilder.build();
    }

    private int[] walkingChangesArray(int arrStationId) {
        int length = timeTable.stations().size();
        int[] walkableChanges = new int[length];
        Arrays.fill(walkableChanges, -1);

        Transfers transfers = timeTable.transfers();
        int arrivingAt = transfers.arrivingAt(arrStationId);

        int startIdx = PackedRange.startInclusive(arrivingAt);
        int endIdx = PackedRange.endExclusive(arrivingAt);
        for (int i = startIdx; i < endIdx; i++) {
            walkableChanges[transfers.depStationId(i)] = transfers.minutes(i);
        }
        return walkableChanges;
    }
}