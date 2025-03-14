package ch.epfl.rechor;

import com.sun.jdi.PrimitiveValue;

/**
 * La classe permet de manipuler une paire de valeur de 24 et 8 bits
 * respectivement empaquetées dans un entier de 32 bits de type int.
 */
public class Bits32_24_8 {
    private static final int LESS_BITS = 8;
    private static final int GREATEST_BITS = 24;
    private static final int LESS_BITS_POSITION = 255;


    private Bits32_24_8(){}

    /**
     *  retourne le vecteur de 32 bits dont les 24 bits de poids fort sont
     *  bits24 et les 8 bits de poids faible sont bits8
     * @param bits24
     * Les 24 bits de poids fort
     * @param bits8
     * Les 8 bits de poids faible
     * @return le vecteur de 32 bits
     */
    public static int pack(int bits24, int bits8){
        Preconditions.checkArgument(bits24 >> GREATEST_BITS==0 && bits8 >> LESS_BITS==0);
        return (bits24 << LESS_BITS) | bits8;
    }

    /**
     * retourne les 24 bits de poids fort du vecteur de 32 bits donné
     * @param bits32
     * Le vecteur de 32 bits
     * @return les 24 bits de poids fort
     */
    public static int unpack24(int bits32){
        return bits32 >>> LESS_BITS;
    }

    /**
     * retourne les 8 bits de poids faible du vecteur de 32 bits donné
     * @param bits32
     * Le vecteur de 32 bits
     * @return les 8 bits de poids faible
     */
    public static int unpack8(int bits32){
        return bits32 & LESS_BITS_POSITION;
    }

}
