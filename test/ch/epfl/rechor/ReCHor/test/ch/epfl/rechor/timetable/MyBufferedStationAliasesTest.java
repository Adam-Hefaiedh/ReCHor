package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MyBufferedStationAliasesTest {

    @Test
    public void constructorCreatesValidObject() {
        List<String> stringTable = Arrays.asList("GVA", "Genève");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        assertDoesNotThrow(() -> new BufferedStationAliases(stringTable, buffer));
    }

    @Test
    public void aliasReturnsCorrectValue() {
        List<String> stringTable = Arrays.asList("GVA", "ZRH", "Genève", "Zürich");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(0, (short) 1); // ALIAS_ID pointe vers "ZRH"

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertEquals("ZRH", aliases.alias(0));
    }

    @Test
    public void stationNameReturnsCorrectValue() {
        List<String> stringTable = Arrays.asList("GVA", "ZRH", "Genève", "Zürich");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(2, (short) 2); // STATION_NAME_ID pointe vers "Genève"

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertEquals("Genève", aliases.stationName(0));
    }

    @Test
    public void sizeReturnsCorrectValue() {
        List<String> stringTable = Arrays.asList("GVA", "Genève");
        ByteBuffer buffer = ByteBuffer.allocate(8); // 2 éléments de 4 octets

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertEquals(2, aliases.size());
    }

    @Test
    public void multipleElementsAccessCorrectly() {
        List<String> stringTable = Arrays.asList("GVA", "ZRH", "Genève", "Zürich");
        ByteBuffer buffer = ByteBuffer.allocate(8);
        // Premier élément
        buffer.putShort(0, (short) 0); // ALIAS_ID = 0 (GVA)
        buffer.putShort(2, (short) 2); // STATION_NAME_ID = 2 (Genève)
        // Deuxième élément
        buffer.putShort(4, (short) 1); // ALIAS_ID = 1 (ZRH)
        buffer.putShort(6, (short) 3); // STATION_NAME_ID = 3 (Zürich)

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals("GVA", aliases.alias(0));
        assertEquals("Genève", aliases.stationName(0));
        assertEquals("ZRH", aliases.alias(1));
        assertEquals("Zürich", aliases.stationName(1));
    }

    @Test
    public void emptyBufferHasZeroSize() {
        List<String> stringTable = Arrays.asList("GVA", "Genève");
        ByteBuffer buffer = ByteBuffer.allocate(0);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertEquals(0, aliases.size());
    }

    @Test
    public void aliasWithNegativeIndexThrowsException() {
        List<String> stringTable = Arrays.asList("GVA", "Genève");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(-1));
    }

    @Test
    public void aliasWithTooLargeIndexThrowsException() {
        List<String> stringTable = Arrays.asList("GVA", "Genève");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(1));
    }

    @Test
    public void stationNameWithNegativeIndexThrowsException() {
        List<String> stringTable = Arrays.asList("GVA", "Genève");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(-1));
    }

    @Test
    public void stationNameWithTooLargeIndexThrowsException() {
        List<String> stringTable = Arrays.asList("GVA", "Genève");
        ByteBuffer buffer = ByteBuffer.allocate(4);

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(1));
    }

    @Test
    public void multipleAliasesForSameStation() {
        List<String> stringTable = Arrays.asList("GVA", "Genève-Aéroport", "Aéroport de Genève", "Genève");
        ByteBuffer buffer = ByteBuffer.allocate(12);
        // Premier élément
        buffer.putShort(0, (short) 0); // ALIAS_ID = 0 (GVA)
        buffer.putShort(2, (short) 3); // STATION_NAME_ID = 3 (Genève)
        // Deuxième élément
        buffer.putShort(4, (short) 1); // ALIAS_ID = 1 (Genève-Aéroport)
        buffer.putShort(6, (short) 3); // STATION_NAME_ID = 3 (Genève)
        // Troisième élément
        buffer.putShort(8, (short) 2); // ALIAS_ID = 2 (Aéroport de Genève)
        buffer.putShort(10, (short) 3); // STATION_NAME_ID = 3 (Genève)

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals("GVA", aliases.alias(0));
        assertEquals("Genève-Aéroport", aliases.alias(1));
        assertEquals("Aéroport de Genève", aliases.alias(2));
        assertEquals("Genève", aliases.stationName(0));
        assertEquals("Genève", aliases.stationName(1));
        assertEquals("Genève", aliases.stationName(2));
    }

    @Test
    public void largeStringTableHandledCorrectly() {
        // Création d'une grande table de chaînes
        String[] array = new String[100];
        for (int i = 0; i < 100; i++) {
            array[i] = "String" + i;
        }
        List<String> stringTable = Arrays.asList(array);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putShort(0, (short) 42);  // ALIAS_ID = 42
        buffer.putShort(2, (short) 73);  // STATION_NAME_ID = 73
        buffer.putShort(4, (short) 18);  // ALIAS_ID = 18
        buffer.putShort(6, (short) 99);  // STATION_NAME_ID = 99

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals("String42", aliases.alias(0));
        assertEquals("String73", aliases.stationName(0));
        assertEquals("String18", aliases.alias(1));
        assertEquals("String99", aliases.stationName(1));
    }
}