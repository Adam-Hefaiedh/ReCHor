package ch.epfl.rechor.ReCHor.test.ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.mapped.Structure;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyStructureTest {

    @Test
    void constructorShouldRejectNonConsecutiveIndices() {
        // Field avec index 0 puis un avec index 2 (missing index 1)
        assertThrows(IllegalArgumentException.class, () -> new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(2, Structure.FieldType.U16)
        ));
    }

    @Test
    void constructorShouldAcceptConsecutiveIndices() {
        // Fields avec index consécutifs
        assertDoesNotThrow(() -> new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        ));
    }

    @Test
    void fieldShouldRejectNullType() {
        assertThrows(NullPointerException.class, () -> Structure.field(0, null));
    }

    @Test
    void totalSizeShouldReturnCorrectSize() {
        // U8 (1 byte) + U16 (2 bytes) + S32 (4 bytes) = 7 bytes
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );
        assertEquals(7, structure.totalSize());

        // Another example with just U16 fields (2 bytes each)
        Structure stationAliases = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.U16)
        );
        assertEquals(4, stationAliases.totalSize());
    }

    @Test
    void offsetShouldReturnCorrectPositions() {
        // Structure avec U8 (1 byte) + U16 (2 bytes) + S32 (4 bytes) = 7 bytes
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );

        // Pour l'élément 0
        assertEquals(0, structure.offset(0, 0)); // champ 0 de l'élément 0
        assertEquals(1, structure.offset(1, 0)); // champ 1 de l'élément 0
        assertEquals(3, structure.offset(2, 0)); // champ 2 de l'élément 0

        // Pour l'élément 1
        assertEquals(7, structure.offset(0, 1));  // champ 0 de l'élément 1
        assertEquals(8, structure.offset(1, 1));  // champ 1 de l'élément 1
        assertEquals(10, structure.offset(2, 1)); // champ 2 de l'élément 1

        // Pour l'élément 2
        assertEquals(14, structure.offset(0, 2)); // champ 0 de l'élément 2
    }

    @Test
    void offsetShouldThrowForInvalidFieldIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        assertThrows(IndexOutOfBoundsException.class, () -> structure.offset(1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> structure.offset(-1, 0));
    }
}
