package ch.epfl.rechor.timetable;

public interface Connections extends Indexed{

    abstract int depStopId(int id);
    abstract int depMins(int id);
    abstract int arrStopId(int id);
    abstract int arrMins(int id);
    abstract int tripId(int id);
    abstract int tripPos(int id);
    abstract int nextConnectionId(int id);

}
