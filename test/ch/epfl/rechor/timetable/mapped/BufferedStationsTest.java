package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferedStationsTest {
    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }

    // Given example
    private static final ByteBuffer STATIONS_1 =
            byteBuffer("00 04 04 b6 ca 14 21 14 1f a1 00 06 04 dc cc 12 21 18 da 03");
    private static final List<String> STRING_TABLE_1 =
            List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

    // Slightly bigger example
    private static final ByteBuffer STATIONS_2 =
            byteBuffer("00 09 04 b6 ca 14 21 14 1f a1 00 06 05 15 ce f1 21 48 40 c9 00 0c 04 dc cc 12 21 18 da 03 00 0b 04 c1 fc fa 21 11 c1 8d");
    private static final List<String> STRING_TABLE_2 =
            List.of("1",
                    "3",
                    "70",
                    "Anet",
                    "Freiburg (CH)",
                    "Fribourg",
                    "Fribourg/Freiburg",
                    "Fribourgo",
                    "Ins",
                    "Lausanne",
                    "Losanna",
                    "Lutry",
                    "Palézieux");

    @Test
    void bufferedStationsSizeWorks() {
        var s1 = new BufferedStations(STRING_TABLE_1, STATIONS_1);
        var s2 = new BufferedStations(STRING_TABLE_2, STATIONS_2);

        assertEquals(2, s1.size());
        assertEquals(4, s2.size());
    }

    @Test
    void bufferedStationsNameThrowsOnInvalidIndex() {
        var s1 = new BufferedPlatforms(STRING_TABLE_1, STATIONS_1);
        var s2 = new BufferedPlatforms(STRING_TABLE_2, STATIONS_2);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            s1.name(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            s1.name(2);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            s2.name(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            s2.name(4);
        });
    }

    @Test
    void bufferedStationsNameWorks() {
        var s1 = new BufferedStations(STRING_TABLE_1, STATIONS_1);
        var s2 = new BufferedStations(STRING_TABLE_2, STATIONS_2);

        assertEquals("Lausanne", s1.name(0));
        assertEquals("Palézieux", s1.name(1));

        assertEquals("Lausanne", s2.name(0));
        assertEquals("Fribourg/Freiburg", s2.name(1));
        assertEquals("Palézieux", s2.name(2));
        assertEquals("Lutry", s2.name(3));
    }

    @Test
    void bufferedStationsLongitudeWorks() {
        var s1 = new BufferedStations(STRING_TABLE_1, STATIONS_1);
        var s2 = new BufferedStations(STRING_TABLE_2, STATIONS_2);

        assertEquals(6.629092, s1.longitude(0), 1e-6);
        assertEquals(6.837875, s1.longitude(1), 1e-6);

        assertEquals(6.629092, s2.longitude(0), 1e-6);
        assertEquals(7.151047, s2.longitude(1), 1e-6);
        assertEquals(6.837875, s2.longitude(2), 1e-6);
        assertEquals(6.690609, s2.longitude(3), 1e-6);
    }

    @Test
    void bufferedStationsLatitudeWorks() {
        var s1 = new BufferedStations(STRING_TABLE_1, STATIONS_1);
        var s2 = new BufferedStations(STRING_TABLE_2, STATIONS_2);

        assertEquals(46.516792, s1.latitude(0), 1e-6);
        assertEquals(46.542764, s1.latitude(1), 1e-6);

        assertEquals(46.516792, s2.latitude(0), 1e-6);
        assertEquals(46.803148, s2.latitude(1), 1e-6);
        assertEquals(46.542764, s2.latitude(2), 1e-6);
        assertEquals(46.503787, s2.latitude(3), 1e-6);
    }
}