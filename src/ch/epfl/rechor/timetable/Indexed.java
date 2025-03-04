package ch.epfl.rechor.timetable;

/**
 * L'interface Indexed représente une collection de données indexées.
 * Les données sont conceptualisées comme étant stockées dans un tableau,
 * avec des index allant de 0 (inclus) à la taille du tableau (exclu).
 */
public interface Indexed {

    /**
     * Retourne la taille de la collection, c.-à-d. le nombre d'éléments indexés.
     *
     * @return le nombre d'éléments dans la collection
     */
    public abstract int size();
}