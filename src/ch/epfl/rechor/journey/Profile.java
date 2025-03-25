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

    public Connections connections(){
        return timeTable.connectionsFor(date);
    }

    Trips trips(){
        return timeTable.tripsFor(date);
    }

    public ParetoFront forStation(int stationId){
        return stationFront.get(stationId);
    }

    public final static class Builder{
        private final TimeTable timeTable;
        private final LocalDate date;
        private final int arrStationId;
        private final ParetoFront.Builder[] stationFrontiers;
        private final ParetoFront.Builder[] tripFrontiers;

        public Builder(TimeTable timeTable, LocalDate date, int arrStationId){
            this.timeTable = timeTable;
            this.date = date;
            this.arrStationId = arrStationId;

            int numberOfStations = timeTable.stations().size();
            stationFrontiers = new ParetoFront.Builder[numberOfStations];

            int numberOfTrips = timeTable.tripsFor(date).size();
            tripFrontiers = new ParetoFront.Builder[numberOfTrips];
        }

        public ParetoFront.Builder forStation(int stationId){
            return stationFrontiers[stationId];
       }

       public void setForStation(int stationId, ParetoFront.Builder builder){
            stationFrontiers[stationId] = builder;
       }

       public ParetoFront.Builder forTrip(int tripId){
            return tripFrontiers[tripId];
       }

       public void setForTrip(int tripId, ParetoFront.Builder builder){
           tripFrontiers[tripId] = builder;
       }
        Profile build(){
            return new Profile(timeTable,date,arrStationId,stationFrontiers);
        }
    }

}