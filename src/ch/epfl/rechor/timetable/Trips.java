package ch.epfl.rechor.timetable;

/**
 * L'interface Trips représente les courses de transport public indexées.
 * Chaque course est considérée comme un élément d'un tableau, identifié par un index allant de 0 à size()-1.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public interface Trips extends Indexed {

    /**
     * Retourne l'index de la ligne (route) à laquelle appartient la course d'index donné.
     *
     * @param id l'index de la course.
     * @return l'index de la ligne associée à la course.
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à zero ou supérieur ou égal à size()).
     */
    int routeId(int id);

    /**
     * Retourne le nom de la destination finale de la course d'index donné.
     *
     * @param id l'index de la course.
     * @return le nom de la destination finale de la course.
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à zero ou supérieur ou égal à size()).
     */
    String destination(int id);
}