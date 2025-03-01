package ch.epfl.rechor.timetable;

public interface Trips extends Indexed{

    abstract int routeId(int id);
    abstract String destination(int id);
}
