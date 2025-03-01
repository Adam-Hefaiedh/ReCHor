package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

public interface Routes extends Indexed {

    abstract Vehicle vehicle(int id);
    abstract String name(int id);

}
