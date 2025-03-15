package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyStructureTest1{

    @Test
    void fieldConstructorThrowsWithNullType() {
        assertThrows(NullPointerException.class, () -> new Structure.Field(0, null));
    }

    @Test
    void staticFieldMethodCreatesField() {
        Structure.Field field = Structure.field(5, Structure.FieldType.U16);
        assertEquals(5, field.index());
        assertEquals(Structure.FieldType.U16, field.type());
    }

    @Test
    void constructorThrowsWithOutOfOrderFields() {
        assertThrows(IllegalArgumentException.class, () ->
                new Structure(
                        Structure.field(1, Structure.FieldType.U8),
                        Structure.field(0, Structure.FieldType.U16)
                )
        );

        assertThrows(IllegalArgumentException.class, () ->
                new Structure(
                        Structure.field(0, Structure.FieldType.U8),
                        Structure.field(2, Structure.FieldType.U16)
                )
        );
    }

    @Test
    void constructorWorksWithCorrectlyOrderedFields() {
        assertDoesNotThrow(() ->
                new Structure(
                        Structure.field(0, Structure.FieldType.U8),
                        Structure.field(1, Structure.FieldType.U16),
                        Structure.field(2, Structure.FieldType.S32)
                )
        );
    }

    @Test
    void totalSizeReturnsCorrectValue() {
        Structure s1 = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        assertEquals(1, s1.totalSize());

        Structure s2 = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        assertEquals(2, s2.totalSize());

        Structure s3 = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        assertEquals(4, s3.totalSize());

        Structure s4 = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );
        assertEquals(1 + 2 + 4, s4.totalSize());
    }

    @Test
    void offsetCalculatesCorrectPositions() {
        Structure s = new Structure(
                Structure.field(0, Structure.FieldType.U8),   // 1 byte
                Structure.field(1, Structure.FieldType.U16),  // 2 bytes
                Structure.field(2, Structure.FieldType.S32)   // 4 bytes
        );

        // For the first element
        assertEquals(0, s.offset(0, 0)); // First field starts at position 0
        assertEquals(1, s.offset(1, 0)); // Second field starts at position 1
        assertEquals(3, s.offset(2, 0)); // Third field starts at position 3

        // For the second element
        int elementSize = 1 + 2 + 4; // 7 bytes per element
        assertEquals(7, s.offset(0, 1));   // First field of second element
        assertEquals(8, s.offset(1, 1));   // Second field of second element
        assertEquals(10, s.offset(2, 1));  // Third field of second element

        // For the third element
        assertEquals(14, s.offset(0, 2));  // First field of third element
        assertEquals(15, s.offset(1, 2));  // Second field of third element
        assertEquals(17, s.offset(2, 2));  // Third field of third element
    }

    @Test
    void offsetThrowsForInvalidFieldIndex() {
        Structure s = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16)
        );

        assertThrows(IndexOutOfBoundsException.class, () -> s.offset(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> s.offset(2, 0));
    }
}