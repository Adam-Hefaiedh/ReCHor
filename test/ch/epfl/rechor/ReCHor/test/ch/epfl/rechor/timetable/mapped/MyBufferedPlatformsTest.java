package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedPlatforms;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.nio.ByteBuffer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MyBufferedPlatformsTest {

    private List<String> stringTable;
    private ByteBuffer platformsBuffer;
    private BufferedPlatforms platforms;

    @BeforeEach
    void setUp() {
        // Prepare a string table with platform names
        stringTable = List.of(
                "1",
                "70",
                "Anet",
                "Ins",
                "Lausanne",
                "Losanna",
                "Palézieux"
        );

        // Create the buffer based on the example in the assignment
        ByteBuffer buffer = ByteBuffer.allocate(12);

        // Platform "1" at Lausanne
        buffer.putShort((short)0);  // String index for "1"
        buffer.putShort((short)0);  // Index of station "Lausanne" (we assume it's 0)

        // Platform "70" at Lausanne
        buffer.putShort((short)1);  // String index for "70"
        buffer.putShort((short)0);  // Index of station "Lausanne" (we assume it's 0)

        // Platform "1" at Palézieux
        buffer.putShort((short)0);  // String index for "1"
        buffer.putShort((short)1);  // Index of station "Palézieux" (we assume it's 1)

        buffer.flip();  // Reset position to beginning
        platformsBuffer = buffer;

        platforms = new BufferedPlatforms(stringTable, platformsBuffer);
    }

    @Test
    void sizeReturnsCorrectNumberOfPlatforms() {
        assertEquals(3, platforms.size());
    }

    @Test
    void nameReturnsCorrectPlatformName() {
        assertEquals("1", platforms.name(0));
        assertEquals("70", platforms.name(1));
        assertEquals("1", platforms.name(2));
    }

    @Test
    void stationIdReturnsCorrectParentStation() {
        assertEquals(0, platforms.stationId(0)); // Lausanne
        assertEquals(0, platforms.stationId(1)); // Lausanne
        assertEquals(1, platforms.stationId(2)); // Palézieux
    }

    @Test
    void nameThrowsOnInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            platforms.name(3); // Only 3 platforms, index 3 is invalid
        });
    }

    @Test
    void stationIdThrowsOnInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            platforms.stationId(3); // Only 3 platforms, index 3 is invalid
        });
    }
}