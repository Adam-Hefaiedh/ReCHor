package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;

import java.nio.ByteBuffer;

/**
 * La classe représente un « tableau d'octets structuré ».
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class StructuredBuffer {

    private final Structure structure;
    private final ByteBuffer buffer;
    private final int size;

    /**
     * Construit un tableau structuré dont les éléments ont la structure donnée,
     * et dont les octets sont stockés dans le « tableau » buffer.
     * @param structure La structure d'element donnée.
     * @param buffer Où les octets sont stockés.
     * @throws IllegalArgumentException si la capaciter du buffer n'est pas divisble par la taille de la structure.
     */
    public StructuredBuffer(Structure structure, ByteBuffer buffer){
       this.structure = structure;
       this.buffer = buffer;
        Preconditions.checkArgument(buffer.capacity() % structure.totalSize() == 0);
        size = buffer.capacity()/ structure.totalSize();
    }

    /**
     * Retourne le nombre d'éléments que contient le tableau.
     * @return le nombre d'éléments du tableau.
     */
    public int size(){
        return size;
    }

    /**
     * Retourne l'entier U8 correspondant au champ d'index fieldIndex
     * de l'élément d'index elementIndex du tableau.
     * @param fieldIndex L'index du champ desirer.
     * @param elementIndex L'index de l'element desirer.
     * @return l'entier U8 correspondant au deux index.
     *
     * @throws IndexOutOfBoundsException si les index sont invalides.
     */
    public int getU8(int fieldIndex, int elementIndex){
        int offset = structure.offset(fieldIndex, elementIndex);
        byte value = buffer.get(offset);
        return Byte.toUnsignedInt(value);
    }

    /**
     * Retourne l'entier U16 correspondant au champ d'index fieldIndex
     * de l'élément d'index elementIndex du tableau.
     * @param fieldIndex L'index du champ desirer.
     * @param elementIndex L'index de l'element desirer.
     * @return l'entier U16 correspondant au deux index.
     *
     * @throws IndexOutOfBoundsException si les index sont invalides.
     */
    public int getU16(int fieldIndex, int elementIndex){
        int offset = structure.offset(fieldIndex, elementIndex);
        short value = buffer.getShort(offset);
        return Short.toUnsignedInt(value);
    }

    /**
     * Retourne l'entier S32 correspondant au champ d'index fieldIndex
     * de l'élément d'index elementIndex du tableau.
     * @param fieldIndex L'index du champ desirer.
     * @param elementIndex L'index de l'element desirer.
     * @return l'entier S32 correspondant au deux index.
     *
     * @throws IndexOutOfBoundsException si les index sont invalides.
     */
    public int getS32(int fieldIndex, int elementIndex){
        int offset = structure.offset(fieldIndex, elementIndex);
        return buffer.getInt(offset);
    }

}
