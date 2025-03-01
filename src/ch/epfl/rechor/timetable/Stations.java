package ch.epfl.rechor.timetable;

public interface Stations extends Indexed{

    abstract String name(int id);
    abstract double longitude(int id);
    abstract double latitude(int id);
}
