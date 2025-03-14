package ch.epfl.rechor.ReCHor.test.ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedPlatforms;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MyBufferedPlatformsTest {
    
    private List<String> stringTable;
    private ByteBuffer platformsBuffer;
    private BufferedPlatforms platforms;
    
    @BeforeEach
    void setUp() {
        // Prepare a string table with names
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
        // Represents three platforms:
        // 1. Platform "1" at station "Lausanne" (station id 0)
        // 2. Platform "70" at station "Lausanne" (station id 0)
        // 3. Platform "1" at station "Palézieux" (station id 1)
        platformsBuffer = ByteBuffer.wrap(new byte[] {
            0x00, 0x00, 0x00, 0x00, // Platform "1" at Lausanne
            0x00, 0x01, 0x00, 0x00, // Platform "70" at Lausanne
            0x00, 0x00, 0x00, 0x01  // Platform "1" at Palézieux
        });
        
        platforms = new BufferedPlatforms(stringTable, platformsBuffer);
    }
    
    @Test
    void sizeShouldReturnCorrectNumberOfPlatforms() {
        assertEquals(3, platforms.size());
    }
    
    @Test
    void nameShouldReturnCorrectPlatformName() {
        assertEquals("1", platforms.name(0));
        assertEquals("70", platforms.name(1));
        assertEquals("1", platforms.name(2));
    }
    
    @Test
    void stationIdShouldReturnCorrectStationIndex() {
        assertEquals(0, platforms.stationId(0)); // Lausanne
        assertEquals(0, platforms.stationId(1)); // Lausanne
        assertEquals(1, platforms.stationId(2)); // Palézieux
    }
    
    @Test
    void methodsShouldThrowForInvalidIndices() {
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.name(3));
        
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.stationId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.stationId(3));
    }
    
    @Test
    void testWithEmptyBuffer() {
        // Create an empty platforms buffer
        ByteBuffer emptyBuffer = ByteBuffer.wrap(new byte[0]);
        BufferedPlatforms emptyPlatforms = new BufferedPlatforms(stringTable, emptyBuffer);
        
        assertEquals(0, emptyPlatforms.size());
    }
}
