package ch.epfl.rechor.timetable;

/**
 * L'interface Stations représente les gares indexées d'un horaire de transport public.
 * Chaque gare est considérée comme un élément d'un tableau, identifié par un index allant de 0 à size()-1.
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public interface Stations extends Indexed {

    /**
     * Retourne le nom de la gare correspondant à l'index donné.
     *
     * @param id l'index de la gare.
     * @return le nom de la gare.
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à zero ou supérieur ou égal à size()).
     */
    String name(int id);

    /**
     * Retourne la longitude, en degrés, de la gare correspondant à l'index donné.
     *
     * @param id l'index de la gare.
     * @return la longitude de la gare en degrés.
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à zero ou supérieur ou égal à size()).
     */
    double longitude(int id);

    /**
     * Retourne la latitude, en degrés, de la gare correspondant à l'index donné.
     *
     * @param id l'index de la gare.
     * @return la latitude de la gare en degrés.
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à zero ou supérieur ou égal à size()).
     */
    double latitude(int id);
}