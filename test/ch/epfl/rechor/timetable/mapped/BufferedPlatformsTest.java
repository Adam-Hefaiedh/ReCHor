package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferedPlatformsTest {
    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }

    // Given example
    private static final ByteBuffer PLATFORMS_1 =
            byteBuffer("00 00 00 00 00 01 00 00 00 00 00 01");
    private static final List<String> STRING_TABLE_1 =
            List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

    // Slightly bigger example
    private static final ByteBuffer PLATFORMS_2 =
            byteBuffer("00 00 00 00 00 02 00 00 00 00 00 03 00 00 00 02");
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
    void bufferedPlatformsSizeWorks() {
        var p1 = new BufferedPlatforms(STRING_TABLE_1, PLATFORMS_1);
        var p2 = new BufferedPlatforms(STRING_TABLE_2, PLATFORMS_2);

        assertEquals(3, p1.size());
        assertEquals(4, p2.size());
    }

    @Test
    void bufferedPlatformsNameThrowsOnInvalidIndex() {
        var p1 = new BufferedPlatforms(STRING_TABLE_1, PLATFORMS_1);
        var p2 = new BufferedPlatforms(STRING_TABLE_2, PLATFORMS_2);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            p1.name(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            p1.name(3);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            p2.name(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            p2.name(4);
        });
    }

    @Test
    void bufferedPlatformsNameWorks() {
        var p1 = new BufferedPlatforms(STRING_TABLE_1, PLATFORMS_1);
        var p2 = new BufferedPlatforms(STRING_TABLE_2, PLATFORMS_2);

        assertEquals("1", p1.name(0));
        assertEquals("70", p1.name(1));
        assertEquals("1", p1.name(2));

        assertEquals("1", p2.name(0));
        assertEquals("70", p2.name(1));
        assertEquals("1", p2.name(2));
        assertEquals("1", p2.name(3));
    }

    @Test
    void bufferedPlatformsStationIdWorks() {
        var p1 = new BufferedPlatforms(STRING_TABLE_1, PLATFORMS_1);
        var p2 = new BufferedPlatforms(STRING_TABLE_2, PLATFORMS_2);

        assertEquals(0, p1.stationId(0));
        assertEquals(0, p1.stationId(1));
        assertEquals(1, p1.stationId(2));

        assertEquals(0, p2.stationId(0));
        assertEquals(0, p2.stationId(1));
        assertEquals(3, p2.stationId(2));
        assertEquals(2, p2.stationId(3));
    }
}