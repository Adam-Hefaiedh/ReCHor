package ch.epfl.rechor.timetable;

public interface Connections extends Indexed{

    //retourne l'index de l'arrêt de départ de la liaison d'index donné,
    abstract int depStopId(int id);

    // retourne l'heure de départ de la liaison d'index donné, exprimée en minutes après minuit,
    abstract int depMins(int id);

    //retourne l'index de l'arrêt d'arrivée de la liaison d'index donné,
    abstract int arrStopId(int id);

    //retourne l'heure d'arrivée de la liaison d'index donné, exprimée en minutes après minuit,
    abstract int arrMins(int id);

    //retourne l'index de la course à laquelle appartient la liaison d'index donné,
    abstract int tripId(int id);

    //retourne la position de la liaison d'index donné dans la course à laquelle elle appartient,
    // la première liaison d'une course ayant l'index 0,
    abstract int tripPos(int id);

    //retourne l'index de la liaison suivant celle d'index donné dans la course à laquelle elle appartient,
    // ou l'index de la première liaison de la course si la liaison d'index donné est la dernière de la course.
    abstract int nextConnectionId(int id);

}
