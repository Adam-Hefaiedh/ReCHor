package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

public interface Routes extends Indexed {

    abstract Vehicle vehicle(int id); //retourne le type de véhicule desservant la ligne d'index donné
    abstract String name(int id); //retourne le nom de la ligne d'index donné (p. ex. IR 15)

}
