package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

/**
 * L'interface Routes représente les lignes de transport public indexées.
 * Les lignes sont conceptualisées comme des éléments d'un tableau dont les indices vont de 0 à size()-1.
 */
public interface Routes extends Indexed {

    /**
     * Retourne le type de véhicule qui dessert la ligne d'index donné.
     *
     * @param id l'index de la ligne
     * @return le véhicule associé à la ligne
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à 0 ou supérieur ou égal à size())
     */
    abstract Vehicle vehicle(int id);

    /**
     * Retourne le nom de la ligne d'index donné, par exemple "IR 15".
     *
     * @param id l'index de la ligne
     * @return le nom de la ligne
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à 0 ou supérieur ou égal à size())
     */
    abstract String name(int id);
}