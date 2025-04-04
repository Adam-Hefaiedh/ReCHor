package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferedStationAliasesTest {
    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }

    // Given example
    private static final ByteBuffer ALIASES_1 =
            byteBuffer("00 05 00 04 00 02 00 03");
    private static final List<String> STRING_TABLE_1 =
            List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

    // Slightly bigger example
    private static final ByteBuffer ALIASES_2 =
            byteBuffer("00 0a 00 09 00 04 00 06 00 05 00 06 00 07 00 06 00 03 00 08");
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
    void bufferedStationAliasesSizeWorks() {
        var a1 = new BufferedStationAliases(STRING_TABLE_1, ALIASES_1);
        var a2 = new BufferedStationAliases(STRING_TABLE_2, ALIASES_2);

        assertEquals(2, a1.size());
        assertEquals(5, a2.size());
    }

    @Test
    void bufferedStationAliasesAliasThrowsOnInvalidIndex() {
        var a1 = new BufferedPlatforms(STRING_TABLE_1, ALIASES_1);
        var a2 = new BufferedPlatforms(STRING_TABLE_2, ALIASES_2);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            a1.name(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            a1.name(2);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            a2.name(-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            a2.name(5);
        });
    }

    @Test
    void bufferedStationAliasesAliasWorks() {
        var a1 = new BufferedStationAliases(STRING_TABLE_1, ALIASES_1);
        var a2 = new BufferedStationAliases(STRING_TABLE_2, ALIASES_2);

        assertEquals("Losanna", a1.alias(0));
        assertEquals("Anet", a1.alias(1));

        assertEquals("Losanna", a2.alias(0));
        assertEquals("Freiburg (CH)", a2.alias(1));
        assertEquals("Fribourg", a2.alias(2));
        assertEquals("Fribourgo", a2.alias(3));
        assertEquals("Anet", a2.alias(4));
    }

    @Test
    void bufferedStationAliasesStationNameWorks() {
        var a1 = new BufferedStationAliases(STRING_TABLE_1, ALIASES_1);
        var a2 = new BufferedStationAliases(STRING_TABLE_2, ALIASES_2);

        assertEquals("Lausanne", a1.stationName(0));
        assertEquals("Ins", a1.stationName(1));

        assertEquals("Lausanne", a2.stationName(0));
        assertEquals("Fribourg/Freiburg", a2.stationName(1));
        assertEquals("Fribourg/Freiburg", a2.stationName(2));
        assertEquals("Fribourg/Freiburg", a2.stationName(3));
        assertEquals("Ins", a2.stationName(4));
    }
}