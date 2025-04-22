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

                paretoBuild.add(allConnection.arrMins(i) + timeTransfers[arrStation],0,i);

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
                                //paretoBuild.add(PackedCriteria.withAdditionalChange(PackedCriteria.withoutDepMins(t)));
                                paretoBuild.add(PackedCriteria.arrMins(t), PackedCriteria.changes(t) + 1, finalI);
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
                                int associatedInformation = Bits32_24_8.pack(finalI, allConnection.tripPos(PackedCriteria.payload(t)) - allConnection.tripPos(finalI));//il manque la position dans la course,
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
