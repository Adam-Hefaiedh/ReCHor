package ch.epfl.rechor.timetable;

public interface Stations extends Indexed{

    abstract String name(int id); //retourne le nom de la gare d'index donné,
    abstract double longitude(int id); //retourne la longitude, en degrés, de la gare d'index donné,
    abstract double latitude(int id); //retourne la latitude, en degrés, de la gare d'index donné.

}
