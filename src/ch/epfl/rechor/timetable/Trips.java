package ch.epfl.rechor.timetable;

public interface Trips extends Indexed{

    abstract int routeId(int id); //retourne l'index de la ligne à laquelle la course d'index donné appartient,
    abstract String destination(int id); //retourne le nom de la destination finale de la course.
}
