package ch.epfl.rechor.journey;
import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.*;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
public record Router(TimeTable timeTable) {
    public Profile profile(LocalDate date , int arrStationId){
        Profile.Builder profileBuilder = new Profile.Builder(timeTable, date, arrStationId);
        Connections connections = timeTable.connectionsFor(date);
        Transfers transfers = timeTable.transfers();
        int[] walkableChanges = walkingChangesArray(arrStationId);
        for (int connectionID = 0; connectionID < connections.size(); connectionID++) {
            ParetoFront.Builder ParetoFront = new ParetoFront.Builder();
            /// Conditions numero 1 :
            int arrStopId = timeTable.stationId(connections.arrStopId(connectionID));
            int depStationId = timeTable.stationId(connections.depStopId(connectionID));


            if(walkableChanges[arrStopId] != -1) {
                int payload = Bits32_24_8.pack(connectionID, 0);
                int arrMin = connections.arrMins(connectionID);
                ParetoFront.add( arrMin + walkableChanges[arrStopId] ,0,payload);
            }


            /// Conditions numero 2 :
            int tripId = connections.tripId(connectionID);
            ParetoFront.Builder tripFront = profileBuilder.forTrip(tripId);
            if (tripFront != null) {
                ParetoFront.addAll(tripFront);
            }

            /// Conditions numero 3 :
            if (profileBuilder.forStation(arrStopId) != null) {
                ParetoFront.Builder arrivalFront = profileBuilder.forStation(arrStopId);
                int finalConnectionID = connectionID; /// Afin d'etre utiliser dans la Lambda
                arrivalFront.forEach(f -> {
                    int depMins = PackedCriteria.depMins(f);
                    if (depMins >= connections.arrMins(finalConnectionID)) {
                        int updatedPayload = Bits32_24_8.pack(finalConnectionID,0);
                        long tuple = PackedCriteria.withoutDepMins(
                                PackedCriteria.withPayload(
                                        PackedCriteria.withAdditionalChange(f),
                                        updatedPayload)
                        );
                        ParetoFront.add(tuple);
                    }
                });
            }
            if(profileBuilder.forTrip(tripId) == null)profileBuilder.setForTrip(tripId, ParetoFront);
            else profileBuilder.forTrip(tripId).addAll(ParetoFront);
            if(ParetoFront.isEmpty()){
                continue;
            }
            /// Mise a jour des tuples
            int inter = transfers.arrivingAt(depStationId);/// Pourquoi l'ensemble des changements de depStation id et pas de Arr ID
            for (int j = PackedRange.startInclusive(inter); j < PackedRange.endExclusive(inter); j++) {
                int d = connections.depMins(connectionID) - transfers.minutes(j);
                ParetoFront.Builder paretoBuildWithDep = new ParetoFront.Builder();
                //MISE A JOUR 2
                int finalConnectionID = connectionID;
                ParetoFront.forEach((t) -> {
                    int intermediatesStop = connections.tripPos(Bits32_24_8.unpack24(PackedCriteria.payload(t))) -
                            connections.tripPos(finalConnectionID);
                    int payload = Bits32_24_8.pack(finalConnectionID, intermediatesStop);
                    long updatesTuple = PackedCriteria.withDepMins(
                            PackedCriteria.withPayload(t,
                                    payload
                            ),
                            d
                    );
                    paretoBuildWithDep.add(updatesTuple);
                });
                if (profileBuilder.forStation(transfers.depStationId(j)) == null)
                    profileBuilder.setForStation(transfers.depStationId(j), paretoBuildWithDep);
                else
                    profileBuilder.forStation(transfers.depStationId(j)).addAll(paretoBuildWithDep);
            }
        }
        return profileBuilder.build();
    }

    private int[] walkingChangesArray(int arrStationId) {
        int length = timeTable.stations().size();
        int[] walkingTimes = new int[length];
        Arrays.fill(walkingTimes,
                -1);
        Transfers transfers = timeTable.transfers();

        int arrivingAt = transfers.arrivingAt(arrStationId);
        int startIdx = PackedRange.startInclusive(arrivingAt);
        int endIdx = PackedRange.endExclusive(arrivingAt);
        for (int i = startIdx; i < endIdx; i++) {
            int depStationId = transfers.depStationId(i);
            int transferTime = transfers.minutes(i);
            walkingTimes[depStationId] = transferTime;
        }
        return walkingTimes;
    }
    static int stationId(Stations stations, String stationName) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.name(i).equals(stationName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Station not found: " + stationName);
    }
    public static void main(String[] args) throws IOException {
        long tStart = System.nanoTime();

        TimeTable timeTable =
                new CachedTimeTable(FileTimeTable.in(Path.of("timetable")));
        Stations stations = timeTable.stations();
        LocalDate date = LocalDate.of(2025, Month.APRIL, 1);
        int depStationId = stationId(stations, "Ecublens VD, EPFL");
        int arrStationId = stationId(stations, "Gruyères");
        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);
        Journey journey = JourneyExtractor
                .journeys(profile, depStationId)
                .get(32);
        System.out.println(JourneyIcalConverter.toIcalendar(journey));

        double elapsed = (System.nanoTime() - tStart) * 1e-9;
        System.out.printf("Temps écoulé : %.3f s%n", elapsed);

    }
}