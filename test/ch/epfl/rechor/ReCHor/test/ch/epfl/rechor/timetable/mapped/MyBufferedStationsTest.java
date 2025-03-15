package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedStations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.nio.ByteBuffer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MyBufferedStationsTest {

    private List<String> stringTable;
    private ByteBuffer stationsBuffer;
    private BufferedStations stations;

    @BeforeEach
    void setUp() {
        // Prepare a string table with station names
        stringTable = List.of(
                "1",
                "70",
                "Anet",
                "Ins",
                "Lausanne",
                "Losanna",
                "Palézieux"
        );

        // Create stations data in the format described in the assignment
        // Convert degrees to the special format integers using 360/2^32 formula
        double factor = Math.pow(2, 32) / 360.0;
        int lausanneLonInt = (int)(6.629092 * factor);
        int lausanneLatInt = (int)(46.516792 * factor);
        int palezieuxLonInt = (int)(6.837875 * factor);
        int palezieuxLatInt = (int)(46.542764 * factor);

        // Allocate a buffer for 2 stations (10 bytes each)
        ByteBuffer buffer = ByteBuffer.allocate(20);

        // Add Lausanne
        buffer.putShort((short)4);  // String index for "Lausanne"
        buffer.putInt(lausanneLonInt);
        buffer.putInt(lausanneLatInt);

        // Add Palézieux
        buffer.putShort((short)6);  // String index for "Palézieux"
        buffer.putInt(palezieuxLonInt);
        buffer.putInt(palezieuxLatInt);

        buffer.flip();  // Reset position to beginning
        stationsBuffer = buffer;

        stations = new BufferedStations(stringTable, stationsBuffer);
    }

    @Test
    void sizeReturnsCorrectNumberOfStations() {
        assertEquals(2, stations.size());
    }

    @Test
    void nameReturnsCorrectStationName() {
        assertEquals("Lausanne", stations.name(0));
        assertEquals("Palézieux", stations.name(1));
    }

    @Test
    void longitudeReturnsCorrectValue() {
        // Allow for small floating-point differences
        assertEquals(6.629092, stations.longitude(0), 0.000001);
        assertEquals(6.837875, stations.longitude(1), 0.000001);
    }

    @Test
    void latitudeReturnsCorrectValue() {
        // Allow for small floating-point differences
        assertEquals(46.516792, stations.latitude(0), 0.000001);
        assertEquals(46.542764, stations.latitude(1), 0.000001);
    }

    @Test
    void nameThrowsOnInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            stations.name(2); // Only 2 stations, index 2 is invalid
        });
    }

    @Test
    void longitudeThrowsOnInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            stations.longitude(2); // Only 2 stations, index 2 is invalid
        });
    }

    @Test
    void latitudeThrowsOnInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            stations.latitude(2); // Only 2 stations, index 2 is invalid
        });
    }
}