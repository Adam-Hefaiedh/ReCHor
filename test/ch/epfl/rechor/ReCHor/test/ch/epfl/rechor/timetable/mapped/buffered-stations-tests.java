package ch.epfl.rechor.ReCHor.test.ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedStations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MyBufferedStationsTest {
    
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
        
        // Use the example from the assignment
        // Represents two stations:
        // 1. Lausanne (6.629092, 46.516792)
        // 2. Palézieux (6.837875, 46.542764)
        stationsBuffer = ByteBuffer.wrap(new byte[] {
            0x00, 0x04, 0x04, (byte)0xb6, (byte)0xca, 0x14, 0x21, 0x14, 0x1f, (byte)0xa1,
            0x00, 0x06, 0x04, (byte)0xdc, (byte)0xcc, 0x12, 0x21, 0x18, (byte)0xda, 0x03
        });
        
        stations = new BufferedStations(stringTable, stationsBuffer);
    }
    
    @Test
    void sizeShouldReturnCorrectNumberOfStations() {
        assertEquals(2, stations.size());
    }
    
    @Test
    void nameShouldReturnCorrectStationName() {
        assertEquals("Lausanne", stations.name(0));
        assertEquals("Palézieux", stations.name(1));
    }
    
    @Test
    void longitudeShouldReturnCorrectValue() {
        // Converting from the raw value to degrees, using the formula:
        // degreeValue = rawValue / 2^24
        
        // First station: Lausanne
        double lausanneLon = 6.629092;
        assertEquals(lausanneLon, stations.longitude(0), 0.000001);
        
        // Second station: Palézieux
        double palezieuxLon = 6.837875;
        assertEquals(palezieuxLon, stations.longitude(1), 0.000001);
    }
    
    @Test
    void latitudeShouldReturnCorrectValue() {
        // First station: Lausanne
        double lausanneLat = 46.516792;
        assertEquals(lausanneLat, stations.latitude(0), 0.000001);
        
        // Second station: Palézieux
        double palezieuxLat = 46.542764;
        assertEquals(palezieuxLat, stations.latitude(1), 0.000001);
    }
    
    @Test
    void methodsShouldThrowForInvalidIndices() {
        assertThrows(IndexOutOfBoundsException.class, () -> stations.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.name(2));
        
        assertThrows(IndexOutOfBoundsException.class, () -> stations.longitude(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.longitude(2));
        
        assertThrows(IndexOutOfBoundsException.class, () -> stations.latitude(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.latitude(2));
    }
}
