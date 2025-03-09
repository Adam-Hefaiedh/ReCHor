package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;

import java.util.Objects;

public class Structure {

    private final int []fieldPosition;
    private final int totalSize;

    public enum FieldType{
        U8,
        U16,
        S32;
    }

    public record Field(int index, FieldType type) {

        public Field{
            Objects.requireNonNull(type);
        }
    }

    public static Field field(int index, FieldType type){
        return new Field(index, type);
    }

    public Structure(Field... fields){

        for (int i = 0; i < fields.length; i++){
            Preconditions.checkArgument(fields[i].index() == i );
        }

        int pos = 0;
        fieldPosition = new int[fields.length];
        int size = 0;

        for (int i = 0; i < fieldPosition.length; i++){
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

    public int TotalSize(){
        return totalSize;
    }

    public int offset(int fieldIndex, int elementIndex){
        return elementIndex * totalSize + fieldPosition[fieldIndex];
    }
}
