package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyBufferedStationAliasesTest2 {

    @Test
    void sizeReturnsCorrectNumberOfAliases() {
        // Based on example in §2.6.2
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        byte[] bytes = new byte[] {
                0, 5,    // Index of "Losanna"
                0, 4,    // Index of "Lausanne"
                0, 2,    // Index of "Anet"
                0, 3     // Index of "Ins"
        };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertEquals(2, aliases.size());
    }

    @Test
    void aliasReturnsCorrectAlternativeName() {
        // Based on example in §2.6.2
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        byte[] bytes = new byte[] {
                0, 5,    // Index of "Losanna"
                0, 4,    // Index of "Lausanne"
                0, 2,    // Index of "Anet"
                0, 3     // Index of "Ins"
        };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertEquals("Losanna", aliases.alias(0));
        assertEquals("Anet", aliases.alias(1));
    }

    @Test
    void stationNameReturnsCorrectOfficialName() {
        // Based on example in §2.6.2
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        byte[] bytes = new byte[] {
                0, 5,    // Index of "Losanna"
                0, 4,    // Index of "Lausanne"
                0, 2,    // Index of "Anet"
                0, 3     // Index of "Ins"
        };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertEquals("Lausanne", aliases.stationName(0));
        assertEquals("Ins", aliases.stationName(1));
    }

    @Test
    void methodsThrowWithInvalidIndex() {
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        byte[] bytes = new byte[] {
                0, 5,    // Index of "Losanna"
                0, 4     // Index of "Lausanne"
        };
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(1));

        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(1));
    }

    @Test
    void hexFormatExample() {
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        // Using HexFormat as suggested in the assignment
        java.util.HexFormat hexFormat = java.util.HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 05 00 04 00 02 00 03");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals(2, aliases.size());
        assertEquals("Losanna", aliases.alias(0));
        assertEquals("Anet", aliases.alias(1));
        assertEquals("Lausanne", aliases.stationName(0));
        assertEquals("Ins", aliases.stationName(1));
    }
}