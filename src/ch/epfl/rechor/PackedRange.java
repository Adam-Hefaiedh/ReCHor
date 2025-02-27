package ch.epfl.rechor;

/**
 * La classe permet de manipuler des intervalles d'entiers
 * empaquetés dans un entier de type int
 */
public final class PackedRange {
    /**
     * Retourne la valeur de type int représentant l'intervalle d'entiers allant
     * de startInclusive (inclus) à endExclusive (exclu)
     * @param startInclusive
     * Valeur minimale de l'intervalle inclus
     * @param endExclusive
     * Valeur maximale de l'intervalle exclu
     * @return la valeur de type int représentant l'intervalle d'entiers
     */
    public static int pack(int startInclusive, int endExclusive)
    {
        int ecart = endExclusive - startInclusive;
        return Bits32_24_8.pack(startInclusive, ecart);
    }

    /**
     * Retourne la longueur de l'intervalle d'entiers empaqueté donné
     * @param interval
     * L'intervalle d'entiers empaqueté
     * @return la longueur de l'intervalle d'entiers empaqueté
     */
    public static int length(int interval){
        return Bits32_24_8.unpack8(interval);
    }

    /**
     * Retourne le début de l'intervalle d'entiers empaqueté donné
     * @param interval
     * L'intervalle d'entiers empaqueté
     * @return le plus petit entier qui fait partie de l'intervalle
     */
    public static int startInclusive(int interval){
        return Bits32_24_8.unpack24(interval);
    }

    /**
     * Retourne le plus petit entier strictement supérieur
     * à tous les entiers de l'intervalle.
     * @param interval
     * L'intervalle d'entiers empaqueté
     * @return Valeur maximale de l'intervalle exclu
     */
    public static int endExclusive(int interval)
    {
        return startInclusive(interval) + length(interval);
    }
}
