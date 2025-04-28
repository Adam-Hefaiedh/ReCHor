package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.*;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Router(TimeTable timeTable) {

    static int stationId(Stations stations, String stationName) {
        for(int i=0; i<stations.size(); i++){
            if(stations.name(i).equals(stationName)){
                return i;
            }
        }
        return -1;
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

    public Profile profile(LocalDate date, int arrStationId)
    {
        Profile.Builder profileBuild = new Profile.Builder(timeTable,date,arrStationId);
        Connections allConnection = timeTable.connectionsFor(date);
        Transfers transfers = timeTable.transfers();
        int[] timeTransfers = new int[timeTable.stations().size()];
        Arrays.fill(timeTransfers, -1);
        int interval =  transfers.arrivingAt(arrStationId);
        for(int i = PackedRange.startInclusive(interval); i < PackedRange.endExclusive(interval); i++)
        {
            timeTransfers[transfers.depStationId(i)] = transfers.minutes(i);
        }

        for(int i=0; i< allConnection.size(); i++){
            int depStationId = timeTable.stationId(allConnection.depStopId(i));
            int arrStation = timeTable.stationId(allConnection.arrStopId(i));
            ParetoFront.Builder paretoBuild = new ParetoFront.Builder();
            if(timeTransfers[arrStation] != -1)
            {

                paretoBuild.add(allConnection.arrMins(i) + timeTransfers[arrStation],0,Bits32_24_8.pack(i,0));

            }
            if(profileBuild.forTrip(allConnection.tripId(i)) != null) {
                paretoBuild.addAll(profileBuild.forTrip(allConnection.tripId(i)));
            }

            int finalI = i;
            if(profileBuild.forStation(arrStation) != null) {
                profileBuild.forStation(arrStation).forEach(
                        (t) -> {
                            int depTime = PackedCriteria.depMins(t);
                            int arrTime = allConnection.arrMins(finalI);
                            if( depTime >= arrTime) {
                                paretoBuild.add(PackedCriteria.withoutDepMins(
                              PackedCriteria.withAdditionalChange(PackedCriteria.withPayload(t,Bits32_24_8.pack(finalI,0)))));
                            }
                        }
                );
            }
            //MISE A JOUR 1
            if (!paretoBuild.isEmpty()) {
                if (profileBuild.forTrip(allConnection.tripId(i)) == null) {
                    profileBuild.setForTrip(allConnection.tripId(i), paretoBuild);
                } else profileBuild.forTrip(allConnection.tripId(i)).addAll(paretoBuild);

                int inter = transfers.arrivingAt( depStationId);
                if(profileBuild.forStation(depStationId) == null || !profileBuild.forStation(depStationId).fullyDominates(paretoBuild,allConnection.depMins(i))) {
                    for (int j = PackedRange.startInclusive(inter); j < PackedRange.endExclusive(inter); j++) {
                        int d = allConnection.depMins(i) - transfers.minutes(j);
                        ParetoFront.Builder paretoBuildWithDep = new ParetoFront.Builder();
                        //MISE A JOUR 2
                            paretoBuild.forEach((t) -> {
                                int associatedInformation = Bits32_24_8.pack(finalI,
                                        allConnection.tripPos(Bits32_24_8.unpack24(PackedCriteria.payload(t)))
                                                - allConnection.tripPos(finalI));//il manque la position dans la course,
                                // de la liason a laquelle il faut descendre de la course.
                                long tWithPayload = PackedCriteria.withPayload(t, associatedInformation);
                                paretoBuildWithDep.add(PackedCriteria.withDepMins(tWithPayload, d));
                            });
                            if (profileBuild.forStation(transfers.depStationId(j)) == null)
                                profileBuild.setForStation(transfers.depStationId(j), paretoBuildWithDep);
                            else profileBuild.forStation(transfers.depStationId(j)).addAll(paretoBuildWithDep);
                    }
                }
            }
        }

        return profileBuild.build();
    }
}
