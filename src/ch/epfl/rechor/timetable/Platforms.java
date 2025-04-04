package ch.epfl.rechor.timetable;

/**
 * L'interface Platforms représente des voies ou quais indexés dans un horaire de transport public.
 * Elle étend l'interface Indexed, ce qui implique que les plateformes sont conceptualisées comme
 * des éléments stockés dans un tableau dont les indices vont de 0 à size()-1.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public interface Platforms extends Indexed {

    /**
     * Retourne le nom de la voie ou du quai correspondant à l'index donné.
     * Par exemple, "70" pour une voie ou "A" pour un quai. La chaîne retournée peut être vide.
     *
     * @param id l'index de la voie ou du quai.
     * @return le nom de la voie ou du quai.
     * @throws IndexOutOfBoundsException si l'index est invalide (inférieur à zero ou supérieur ou égal à size()).
     */
    String name(int id);

    /**
     * Retourne l'index de la gare à laquelle appartient la voie ou le quai identifié par l'index donné.
     *
     * @param id l'index de la voie ou du quai.
     * @return l'index de la gare associée.
     * @throws IndexOutOfBoundsException si l'index est invalide (inférieur à zero ou supérieur ou égal à size()).
     */
    int stationId(int id);
}