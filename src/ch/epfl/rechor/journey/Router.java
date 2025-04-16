package ch.epfl.rechor.journey;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.*;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

public record Router(TimeTable timeTable) {

    public static void main(String[] args) throws  IOException {
        long tStart = System.nanoTime();

        TimeTable timeTable = FileTimeTable.in(Path.of("timetable"));
        Stations stations = timeTable.stations();
        LocalDate date = LocalDate.of(2025, Month.APRIL, 1);
        int depStationId = 7862;
        int arrStationId = 11440;
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
            ParetoFront.Builder paretoBuild = new ParetoFront.Builder();
            if(timeTransfers[timeTable.stationId(allConnection.arrStopId(i))] != -1)
            {
                paretoBuild.add(allConnection.arrMins(i) + timeTransfers[timeTable.stationId(allConnection.arrStopId(i))],0,0);
            }
            if(profileBuild.forTrip(allConnection.tripId(i)) != null) {
                paretoBuild.addAll(profileBuild.forTrip(allConnection.tripId(i)));
            }

            int finalI = i;
            if(profileBuild.forStation(timeTable.stationId(allConnection.arrStopId(i))) != null) {
                profileBuild.forStation(timeTable.stationId(allConnection.arrStopId(i))).forEach(
                        (t) -> {
                            int depTime = PackedCriteria.depMins(t);
                            int arrTime = allConnection.arrMins(finalI);
                            if( depTime >= arrTime) {
                                paretoBuild.add(PackedCriteria.withAdditionalChange(PackedCriteria.withoutDepMins(t)));
                            }
                        }
                );
            }
            if(profileBuild.forTrip(allConnection.tripId(i)) == null)profileBuild.setForTrip(allConnection.tripId(i),paretoBuild);
            else profileBuild.forTrip(allConnection.tripId(i)).addAll(paretoBuild);

            int inter = transfers.arrivingAt(timeTable.stationId(allConnection.depStopId(i)));
            for(int j= PackedRange.startInclusive(inter); j <PackedRange.endExclusive(inter); j++)
            {
                int d = allConnection.depMins(i) - transfers.minutes(j);
                ParetoFront.Builder paretoBuildWithDep = new ParetoFront.Builder();
                paretoBuild.forEach((t) -> paretoBuildWithDep.add(PackedCriteria.withDepMins(t,d)));
                if(profileBuild.forStation(transfers.depStationId(j)) == null) profileBuild.setForStation(transfers.depStationId(j), paretoBuildWithDep);
                else profileBuild.forStation(transfers.depStationId(j)).addAll( paretoBuildWithDep);
            }
        }

        return profileBuild.build();
    }
}
