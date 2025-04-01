package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;

import java.util.Objects;

/**
 * La classe a pour but de faciliter la description de la structure des données aplatie
 */
public class Structure {

    private final int[] fieldPosition;
    private final int totalSize;

    /**
     * Classe representant les differentes tailles de champ
     */
    public enum FieldType{
        U8,
        U16,
        S32
    }

    /**
     * La classe représente un champ et possède les deux attributs suivants
     * @param index
     * L'index du champ dans la structure
     * @param type
     * Le type du champ
     *
     */
    public record Field(int index, FieldType type) {
        /**
         * Verifie si le type est null ou pas
         * @param index
         * index du champ dans le tableau
         * @param type
         * type du champ
         *
         * @throws NullPointerException si le type du champ est null
         */
        public Field{
            Objects.requireNonNull(type);
        }
    }

    /**
     * Retourne une instance de Field avec les attributs donnés
     * @param index
     * L'index du champ dans la structure
     * @param type
     * Le type du champ
     * @return une instance de Field
     */
    public static Field field(int index, FieldType type){
        return new Field(index, type);
    }

    /**
     * Retourne une structure dont les champs sont ceux donnés
     * @param fields
     * liste des champs donner
     * @throws IllegalArgumentException si les index de fields ne sont pas dans l'ordre
     */
    public Structure(Field... fields){
        int pos = 0;
        fieldPosition = new int[fields.length];
        int size = 0;
        for (int i = 0; i < fields.length; i++){
            Preconditions.checkArgument(fields[i].index() == i );
            fieldPosition[i] = pos;
            switch (fields[i].type()){
                case U8 -> size = 1;
                case U16 -> size = 2;
                case S32 -> size = 4;
            }
            pos += size;
        }
        totalSize = pos;
    }

    /**
     * Retourne la taille totale, en octets, de la structure
     * @return la taille totale de la structure
     */
    public int totalSize(){
        return totalSize;
    }

    /**
     *  Retourne l'index, dans le tableau d'octets contenant les données aplaties,
     *  du premier octet du champ d'index fieldIndex de l'élément d'index elementIndex
     * @param fieldIndex
     * L'index du champ desirer
     * @param elementIndex
     * L'index d'element desirer
     * @return l'index de la donner desirer dans le tableau
     *
     * @throws IndexOutOfBoundsException si l'index de champ est invalide
     */
    public int offset(int fieldIndex, int elementIndex){
        return elementIndex * totalSize + fieldPosition[fieldIndex];
    }
}
