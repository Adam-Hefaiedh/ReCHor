package ch.epfl.rechor.timetable;

public interface StationAliases extends Indexed{

    abstract String alias(int id);
    abstract String stationName(int id);

}
