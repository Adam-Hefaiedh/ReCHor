package ch.epfl.rechor.ReCHor.test.ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedStationAliases;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MyBufferedStationAliasesTest {
    
    private List<String> stringTable;
    private ByteBuffer aliasesBuffer;
    private BufferedStationAliases aliases;
    
    @BeforeEach
    void setUp() {
        // Prepare a string table with station names and aliases
        stringTable = List.of(
            "1",
            "70",
            "Anet",
            "Ins",
            "Lausanne",
            "Losanna",
            "Palézieux"
        );
        
        // Use the example from the assignment
        // Represents two aliases:
        // 1. Losanna -> Lausanne
        // 2. Anet -> Ins
        aliasesBuffer = ByteBuffer.wrap(new byte[] {
            0x00, 0x05, 0x00, 0x04, // Losanna -> Lausanne
            0x00, 0x02, 0x00, 0x03  // Anet -> Ins
        });
        
        aliases = new BufferedStationAliases(stringTable, aliasesBuffer);
    }
    
    @Test
    void sizeShouldReturnCorrectNumberOfAliases() {
        assertEquals(2, aliases.size());
    }
    
    @Test
    void aliasShouldReturnCorrectAlternativeName() {
        assertEquals("Losanna", aliases.alias(0));
        assertEquals("Anet", aliases.alias(1));
    }
    
    @Test
    void stationNameShouldReturnCorrectOfficialName() {
        assertEquals("Lausanne", aliases.stationName(0));
        assertEquals("Ins", aliases.stationName(1));
    }
    
    @Test
    void methodsShouldThrowForInvalidIndices() {
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(2));
        
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(2));
    }
    
    @Test
    void testWithEmptyBuffer() {
        // Create an empty aliases buffer
        ByteBuffer emptyBuffer = ByteBuffer.wrap(new byte[0]);
        BufferedStationAliases emptyAliases = new BufferedStationAliases(stringTable, emptyBuffer);
        
        assertEquals(0, emptyAliases.size());
    }
}
