package ch.epfl.rechor.timetable;

public interface  Platforms extends Indexed{

    abstract String name(int id); //retourne le nom de la voie (p. ex. 70) ou du quai (p. ex. A), qui peut être vide
    abstract int stationId(int id); //retourne l'index de la gare à laquelle cette voie ou ce quai appartient

}
