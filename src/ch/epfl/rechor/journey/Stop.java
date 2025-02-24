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
        Preconditions.checkArgument((-180<=longitude && longitude<=180 )&&(-90<=latitude && latitude<=90 ));
    }
}
