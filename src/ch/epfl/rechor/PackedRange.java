package ch.epfl.rechor;

/**
 * La classe permet de manipuler des intervalles d'entiers
 * empaquetés dans un entier de type int.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class PackedRange {

    private PackedRange() {
    }

    /**
     * Retourne la valeur de type int représentant l'intervalle d'entiers allant
     * de startInclusive (inclus) à endExclusive (exclu).
     *
     * @param startInclusive Valeur minimale de l'intervalle inclus.
     * @param endExclusive   Valeur maximale de l'intervalle exclu.
     * @return la valeur de type int représentant l'intervalle d'entiers.
     * @throws IllegalArgumentException si length et startInclusive ne sont pas de la bonne taille.
     */
    public static int pack(int startInclusive, int endExclusive) {
        int length = endExclusive - startInclusive;
        return Bits32_24_8.pack(startInclusive, length);
    }

    /**
     * Retourne la longueur de l'intervalle d'entiers empaqueté donné.
     *
     * @param interval L'intervalle d'entiers empaqueté.
     * @return la longueur de l'intervalle d'entiers empaqueté.
     */
    public static int length(int interval) {
        return Bits32_24_8.unpack8(interval);
    }

    /**
     * Retourne le début de l'intervalle d'entiers empaqueté donné.
     *
     * @param interval L'intervalle d'entiers empaqueté.
     * @return le plus petit entier qui fait partie de l'intervalle.
     */
    public static int startInclusive(int interval) {
        return Bits32_24_8.unpack24(interval);
    }

    /**
     * Retourne le plus petit entier strictement supérieur à tous les entiers de l'intervalle.
     *
     * @param interval L'intervalle d'entiers empaqueté.
     * @return Valeur maximale de l'intervalle exclu.
     */
    public static int endExclusive(int interval) {

        return startInclusive(interval) + length(interval);
    }
}
