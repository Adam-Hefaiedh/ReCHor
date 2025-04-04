package ch.epfl.rechor;


/**
 * La classe permet de manipuler une paire de valeur de 24 et 8 bits
 * respectivement empaquetés dans un entier de 32 bits de type int.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class Bits32_24_8 {
    private static final int LESS_BITS = 8;
    private static final int GREATEST_BITS = 24;
    private static final int LESS_BITS_SIZE = 255;


    private Bits32_24_8(){}

    /**
     *  Retourne le vecteur de 32 bits dont les 24 bits de poids fort sont
     *  bits24 et les 8 bits de poids faible sont bits8.
     * @param bits24 Les 24 bits de poids fort.
     * @param bits8 Les 8 bits de poids faible.
     * @return le vecteur de 32 bits.
     * @throws IllegalArgumentException si bits24 et bits8 ne sont pas de la bonne taille.
     */
    public static int pack(int bits24, int bits8){
        Preconditions.checkArgument(bits24 >> GREATEST_BITS==0 && bits8 >> LESS_BITS==0);
        return (bits24 << LESS_BITS) | bits8;
    }

    /**
     * Retourne les 24 bits de poids fort du vecteur de 32 bits donné.
     * @param bits32 Le vecteur de 32 bits.
     * @return les 24 bits de poids fort.
     */
    public static int unpack24(int bits32){
        return bits32 >>> LESS_BITS;
    }

    /**
     * Retourne les 8 bits de poids faible du vecteur de 32 bits donné.
     * @param bits32 Le vecteur de 32 bits.
     * @return les 8 bits de poids faible.
     */
    public static int unpack8(int bits32){
        return bits32 & LESS_BITS_SIZE;
    }

}
