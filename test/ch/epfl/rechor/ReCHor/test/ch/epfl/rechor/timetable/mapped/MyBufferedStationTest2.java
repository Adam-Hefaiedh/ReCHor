package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyBufferedStationsTest2 {

    @Test
    void sizeReturnsCorrectNumberOfStations() {
        // Based on example in §2.6.1
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        byte[] bytes = new byte[] {
                0, 4,                   // Name ID for "Lausanne"
                4, (byte)0xB6, (byte)0xCA, 0x14,  // Longitude
                0x21, 0x14, 0x1F, (byte)0xA1,     // Latitude
                0, 6,                   // Name ID for "Palézieux"
                4, (byte)0xDC, (byte)0xCC, 0x12,  // Longitude
                0x21, 0x18, (byte)0xDA, 0x03      // Latitude
        };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals(2, stations.size());
    }

    @Test
    void nameReturnsCorrectStationName() {
        // Based on example in §2.6.1
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        byte[] bytes = new byte[] {
                0, 4,                   // Name ID for "Lausanne"
                4, (byte)0xB6, (byte)0xCA, 0x14,  // Longitude
                0x21, 0x14, 0x1F, (byte)0xA1,     // Latitude
                0, 6,                   // Name ID for "Palézieux"
                4, (byte)0xDC, (byte)0xCC, 0x12,  // Longitude
                0x21, 0x18, (byte)0xDA, 0x03      // Latitude
        };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals("Lausanne", stations.name(0));
        assertEquals("Palézieux", stations.name(1));
    }

    @Test
    void longitudeReturnsCorrectValue() {
        // Based on example in §2.6.1
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        byte[] bytes = new byte[] {
                0, 4,                   // Name ID for "Lausanne"
                4, (byte)0xB6, (byte)0xCA, 0x14,  // Longitude
                0x21, 0x14, 0x1F, (byte)0xA1,     // Latitude
                0, 6,                   // Name ID for "Palézieux"
                4, (byte)0xDC, (byte)0xCC, 0x12,  // Longitude
                0x21, 0x18, (byte)0xDA, 0x03      // Latitude
        };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals(6.629092, stations.longitude(0), 0.000001);
        assertEquals(6.837875, stations.longitude(1), 0.000001);
    }

    @Test
    void latitudeReturnsCorrectValue() {
        // Based on example in §2.6.1
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        byte[] bytes = new byte[] {
                0, 4,                   // Name ID for "Lausanne"
                4, (byte)0xB6, (byte)0xCA, 0x14,  // Longitude
                0x21, 0x14, 0x1F, (byte)0xA1,     // Latitude
                0, 6,                   // Name ID for "Palézieux"
                4, (byte)0xDC, (byte)0xCC, 0x12,  // Longitude
                0x21, 0x18, (byte)0xDA, 0x03      // Latitude
        };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals(46.516792, stations.latitude(0), 0.000001);
        assertEquals(46.542764, stations.latitude(1), 0.000001);
    }

    @Test
    void methodsThrowWithInvalidIndex() {
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        byte[] bytes = new byte[] {
                0, 4,                   // Name ID for "Lausanne"
                4, (byte)0xB6, (byte)0xCA, 0x14,  // Longitude
                0x21, 0x14, 0x1F, (byte)0xA1,     // Latitude
        };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStations stations = new BufferedStations(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> stations.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.name(1));

        assertThrows(IndexOutOfBoundsException.class, () -> stations.longitude(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.longitude(1));

        assertThrows(IndexOutOfBoundsException.class, () -> stations.latitude(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.latitude(1));
    }

    @Test
    void hexFormatExample() {
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        // Using HexFormat as suggested in the assignment
        java.util.HexFormat hexFormat = java.util.HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 04 04 b6 ca 14 21 14 1f a1 00 06 04 dc cc 12 21 18 da 03");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStations stations = new BufferedStations(stringTable, buffer);

        assertEquals(2, stations.size());
        assertEquals("Lausanne", stations.name(0));
        assertEquals("Palézieux", stations.name(1));
        assertEquals(6.629092, stations.longitude(0), 0.000001);
        assertEquals(6.837875, stations.longitude(1), 0.000001);
        assertEquals(46.516792, stations.latitude(0), 0.000001);
        assertEquals(46.542764, stations.latitude(1), 0.000001);
    }
}