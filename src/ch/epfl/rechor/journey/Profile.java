package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.time.LocalDate;
import java.util.List;

public record Profile (TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {

    public Profile {
        stationFront = List.copyOf(stationFront);
    }

    Connections connections(){}

    Trips trips(){}

    ParetoFront forStation(int stationId){
        return stationFront.get(stationId);
    }

    public static final class Builder{

        Builder(TimeTable timeTable, LocalDate date, int arrStationId){
            
        }
    }

}