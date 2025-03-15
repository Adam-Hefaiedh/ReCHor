package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MyBufferedPlatformsTest {

    /**
     * Vérifie que le constructeur de BufferedPlatforms crée un objet valide.
     */
    @Test
    public void constructorCreatesValidObject() {
        List<String> stringTable = Arrays.asList("Quai A", "Quai B");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        assertDoesNotThrow(() -> new BufferedPlatforms(stringTable, buffer));
    }

    /**
     * Vérifie que la méthode name retourne la bonne valeur.
     * Ici, NAME_ID vaut 2 et pointe vers "Quai C" dans la table des chaînes.
     */
    @Test
    public void nameReturnsCorrectValue() {
        List<String> stringTable = Arrays.asList("Quai A", "Quai B", "Quai C");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(0, (short) 2);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals("Quai C", platforms.name(0));
    }

    /**
     * Vérifie que la méthode stationId retourne la bonne valeur.
     */
    @Test
    public void stationIdReturnsCorrectValue() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        // Écriture à l'offset 2 pour le champ STATION_ID
        buffer.putShort(2, (short) 42);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals(42, platforms.stationId(0));
    }

    /**
     * Vérifie que la méthode size retourne la taille correcte.
     * Ici, le buffer contient 2 enregistrements de 4 octets chacun.
     */
    @Test
    public void sizeReturnsCorrectValue() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(8);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals(2, platforms.size());
    }

    /**
     * Vérifie que l'accès à plusieurs éléments se fait correctement.
     */
    @Test
    public void multipleElementsAccessCorrectly() {
        List<String> stringTable = Arrays.asList("Quai A", "Quai B", "Quai C");
        ByteBuffer buffer = ByteBuffer.allocate(8);
        // Premier enregistrement
        buffer.putShort(0, (short) 0);  // NAME_ID = 0 ("Quai A")
        buffer.putShort(2, (short) 10); // STATION_ID = 10
        // Deuxième enregistrement
        buffer.putShort(4, (short) 1);  // NAME_ID = 1 ("Quai B")
        buffer.putShort(6, (short) 20); // STATION_ID = 20
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals("Quai A", platforms.name(0));
        assertEquals(10, platforms.stationId(0));
        assertEquals("Quai B", platforms.name(1));
        assertEquals(20, platforms.stationId(1));
    }

    /**
     * Vérifie qu'un buffer vide donne une taille de zéro.
     */
    @Test
    public void emptyBufferHasZeroSize() {
        List<String> stringTable = Collections.singletonList("Vide");
        ByteBuffer buffer = ByteBuffer.allocate(0);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertEquals(0, platforms.size());
    }

    /**
     * Vérifie que la méthode name lance une exception si l'indice est négatif.
     */
    @Test
    public void nameWithNegativeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.name(-1));
    }

    /**
     * Vérifie que la méthode name lance une exception si l'indice est trop grand.
     */
    @Test
    public void nameWithTooLargeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.name(1));
    }

    /**
     * Vérifie que la méthode stationId lance une exception si l'indice est négatif.
     */
    @Test
    public void stationIdWithNegativeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.stationId(-1));
    }

    /**
     * Vérifie que la méthode stationId lance une exception si l'indice est trop grand.
     */
    @Test
    public void stationIdWithTooLargeIndexThrowsException() {
        List<String> stringTable = Collections.singletonList("Quai");
        ByteBuffer buffer = ByteBuffer.allocate(4);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.stationId(1));
    }

    /**
     * Vérifie que si le NAME_ID lu ne correspond pas à un index valide dans la table des chaînes,
     * la méthode name lance une exception.
     */
    @Test
    public void invalidNameIdThrowsException() {
        List<String> stringTable = Arrays.asList("Quai A", "Quai B");
        // Ici, NAME_ID = 2 est invalide car la table ne contient que les indices 0 et 1.
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(0, (short) 2);
        BufferedPlatforms platforms = new BufferedPlatforms(stringTable, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> platforms.name(0));
    }
}