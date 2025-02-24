package ch.epfl.rechor.journey;

import java.util.List;

/**
 * Class representant les differents types de vehicule.
 */
public enum Vehicle {
    TRAM,
    METRO,
    TRAIN,
    BUS,
    FERRY,
    AERIAL_LIFT,
    FUNICULAR;
    public static final List<Vehicle> ALL = List.of(values());
}
