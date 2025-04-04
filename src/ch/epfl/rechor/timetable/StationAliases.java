package ch.epfl.rechor.timetable;

/**
 * L'interface StationAliases représente les alias ou noms alternatifs des gares indexées.
 * Les alias sont conceptualisés comme des éléments d'un tableau dont les indices vont de 0 à size()-1.
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public interface StationAliases extends Indexed {

    /**
     * Retourne le nom alternatif de la gare pour l'index donné.
     * Par exemple, "Losanna" peut être un alias pour "Lausanne".
     *
     * @param id l'index du nom alternatif.
     * @return le nom alternatif de la gare.
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à 0 ou supérieur ou égal à size()).
     */
    String alias(int id);

    /**
     * Retourne le nom officiel de la gare correspondant à l'alias d'index donné.
     * Par exemple, pour l'alias "Losanna", cette méthode peut retourner "Lausanne".
     *
     * @param id l'index du nom alternatif.
     * @return le nom officiel de la gare associée à l'alias.
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à 0 ou supérieur ou égal à size()).
     */
    String stationName(int id);
}