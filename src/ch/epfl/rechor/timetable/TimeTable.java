package ch.epfl.rechor.timetable;

import java.time.LocalDate;

public interface TimeTable {

    abstract Stations stations();
    abstract StationAliases stationAliases();
    abstract Platforms platforms();
    abstract Routes routes();
    abstract Transfers transfers();
    abstract Trips tripsFor(LocalDate date);
    abstract Connections connectionsFor(LocalDate date);

    default boolean isStationId(int stopId){
       return !stations().name(stopId).isEmpty();
    }
    default boolean isPlatformId(int stopId){
        return !platforms().name(stopId).isEmpty();
    }
    default int stationId(int stopId) {
        if (isStationId(stopId) == true) {
            return stopId;
        }
        return stopId;
    }
    default String platformName (int stopId){
        if (isPlatformId(stopId) == true) {
            return platforms().name(stopId);
        }
        return null;
    }

}
