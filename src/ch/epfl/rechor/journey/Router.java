package ch.epfl.rechor.journey;
import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.*;
import java.time.LocalDate;
import java.util.Arrays;

public record Router(TimeTable timeTable) {
    public Profile profile(LocalDate date , int arrStationId){
        Profile.Builder profileBuilder = new Profile.Builder(timeTable, date, arrStationId);
        Connections connections = timeTable.connectionsFor(date);
        Transfers transfers = timeTable.transfers();
        int[] walkableChanges = walkingChangesArray(arrStationId);
        for (int connectionID = 0; connectionID < connections.size(); connectionID++) {
            ParetoFront.Builder paretoBuild = new ParetoFront.Builder();
            int arrStopId = timeTable.stationId(connections.arrStopId(connectionID));
            int depStationId = timeTable.stationId(connections.depStopId(connectionID));
            int currentConnection = connectionID;

            if(walkableChanges[arrStopId] != -1) {
                int payload = Bits32_24_8.pack(connectionID, 0);
                int arrMin = connections.arrMins(connectionID);
                paretoBuild.add( arrMin + walkableChanges[arrStopId] ,0,payload);
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
                if(profileBuilder.forStation(depStationId) != null && profileBuilder.forStation(depStationId).fullyDominates(paretoBuild, connections.depMins(connectionID))) {
                    for (int j = PackedRange.startInclusive(inter); j < PackedRange.endExclusive(inter); j++) {
                        int d = connections.depMins(connectionID) - transfers.minutes(j);
                        ParetoFront.Builder paretoBuildWithDep = new ParetoFront.Builder();
                        paretoBuild.forEach((t) -> {
                            int intermediatesStop = connections.tripPos(Bits32_24_8.unpack24(PackedCriteria.payload(t))) -
                                    connections.tripPos(currentConnection);
                            int payload = Bits32_24_8.pack(currentConnection, intermediatesStop);
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
            }
        }
        return profileBuilder.build();
    }

    private int[] walkingChangesArray(int arrStationId) {
        int length = timeTable.stations().size();
        int[] walkableChanges = new int[length];
        Arrays.fill(walkableChanges,
                -1);
        Transfers transfers = timeTable.transfers();

        int arrivingAt = transfers.arrivingAt(arrStationId);
        int startIdx = PackedRange.startInclusive(arrivingAt);
        int endIdx = PackedRange.endExclusive(arrivingAt);
        for (int i = startIdx; i < endIdx; i++) {
            int depStationId = transfers.depStationId(i);
            int transferTime = transfers.minutes(i);
            walkableChanges[depStationId] = transferTime;
        }
        return walkableChanges;
    }
}