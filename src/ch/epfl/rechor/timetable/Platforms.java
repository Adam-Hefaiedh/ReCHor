package ch.epfl.rechor.timetable;

public interface Platforms extends Indexed{

    abstract String name(int id);
    abstract int stationId(int id);

}
