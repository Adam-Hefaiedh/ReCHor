package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedStationAliases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.nio.ByteBuffer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MyBufferedStationAliasesTest {

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

        // Create the buffer based on the example in the assignment
        ByteBuffer buffer = ByteBuffer.allocate(8);

        // Add Losanna -> Lausanne
        buffer.putShort((short)5);  // String index for "Losanna"
        buffer.putShort((short)4);  // String index for "Lausanne"

        // Add Anet -> Ins
        buffer.putShort((short)2);  // String index for "Anet"
        buffer.putShort((short)3);  // String index for "Ins"

        buffer.flip();  // Reset position to beginning
        aliasesBuffer = buffer;

        aliases = new BufferedStationAliases(stringTable, aliasesBuffer);
    }

    @Test
    void sizeReturnsCorrectNumberOfAliases() {
        assertEquals(2, aliases.size());
    }

    @Test
    void aliasReturnsCorrectAlias() {
        assertEquals("Losanna", aliases.alias(0));
        assertEquals("Anet", aliases.alias(1));
    }

    @Test
    void stationNameReturnsCorrectName() {
        assertEquals("Lausanne", aliases.stationName(0));
        assertEquals("Ins", aliases.stationName(1));
    }

    @Test
    void aliasThrowsOnInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            aliases.alias(2); // Only 2 aliases, index 2 is invalid
        });
    }

    @Test
    void stationNameThrowsOnInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            aliases.stationName(2); // Only 2 aliases, index 2 is invalid
        });
    }
}