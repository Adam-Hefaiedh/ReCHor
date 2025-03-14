package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.util.Objects;


/**
 * Class representant les arrêts de transport public.
 *
 * @param name
 * Le nom de l'arrêt, ou le nom de la gare à laquelle appartient l'arrêt.
 *
 * @param platformName
 * le nom de la voie ou du quai de l'arrêt, ou null si l'arrêt correspond à une gare,
 *
 * @param longitude
 * la longitude de la position de l'arrêt, en degrés,
 *
 * @param latitude
 * la latitude de la position de l'arrêt, en degrés,
 *
 */
public record Stop(String name, String platformName, double longitude, double latitude) {
    private final static int MAX_LONGITUDE = 180;
    private final static int MIN_LONGITUDE = -180;
    private final static int MAX_LATITUDE = 90;
    private final static int MIN_LATITUDE = -90;

    /**
     * constructeur verifiant si le nom de l'arret n'est pas null et que
     * la longitude soit entre -180 et 180 (inclus) et la latitude entre
     * -90 et 90 (inclus)
     *
     * @param name
     * Le nom de l'arrêt, ou le nom de la gare à laquelle appartient l'arrêt.
     *
     * @param platformName
     * le nom de la voie ou du quai de l'arrêt, ou null si l'arrêt correspond à une gare,
     *
     * @param longitude
     * la longitude de la position de l'arrêt, en degrés,
     *
     * @param latitude
     * la latitude de la position de l'arrêt, en degrés,
     *
     */
    public Stop{
        Objects.requireNonNull(name);
        Preconditions.checkArgument((MIN_LONGITUDE<=longitude && longitude<=MAX_LONGITUDE )&&(MIN_LATITUDE<=latitude && latitude<=MAX_LATITUDE ));
    }
}
