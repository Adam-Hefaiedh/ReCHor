package ch.epfl.rechor.timetable;

public interface StationAliases extends Indexed{

    abstract String alias(int id); //retourne le nom alternatif d'index donné (p. ex. Losanna)
    abstract String stationName(int id); //retourne le nom de la gare à laquelle correspond le nom alternatif d'index donné (p. ex. Lausanne).

}
