package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cette classe de test utilitaire peut vous aider à créer et convertir des données hexadécimales
 * pour tester vos classes BufferedRoutes, BufferedTrips, BufferedConnections et BufferedTransfers.
 */
class StructureUtilsTest {

    @Test
    void hexStringConversionTest() {
        // Ce test montre comment convertir une chaîne hexadécimale en tableau d'octets
        // et comment convertir un tableau d'octets en chaîne hexadécimale
        
        String hexString = "00 05 00 04 00 02 00 03";
        
        // Convertir une chaîne hexadécimale en tableau d'octets
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex(hexString);
        
        // Vérifier le contenu du tableau d'octets
        byte[] expected = {0, 5, 0, 4, 0, 2, 0, 3};
        assertArrayEquals(expected, bytes);
        
        // Convertir le tableau d'octets en ByteBuffer
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        
        // Vérifier que le contenu du ByteBuffer est correct
        byte[] fromBuffer = new byte[buffer.capacity()];
        buffer.get(fromBuffer);
        buffer.rewind(); // Remettre la position à 0 pour une utilisation ultérieure
        assertArrayEquals(expected, fromBuffer);
        
        // Convertir un tableau d'octets en chaîne hexadécimale (pour le débogage)
        String backToHex = hexFormat.formatHex(bytes);
        assertEquals(hexString, backToHex);
    }

    @Test
    void readingStructuredDataTest() {
        // Ce test montre comment lire différents types de données à partir d'un ByteBuffer
        
        // Exemple de données: U16, U8, U16, S32
        byte[] bytes = HexFormat.ofDelimiter(" ").parseHex("01 23 45 67 89 ff ff ff ff");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        
        // Lire U16 (2 octets)
        int u16Value = Short.toUnsignedInt(buffer.getShort(0));
        assertEquals(0x0123, u16Value);
        
        // Lire U8 (1 octet)
        int u8Value = Byte.toUnsignedInt(buffer.get(2));
        assertEquals(0x45, u8Value);
        
        // Lire un autre U16 (2 octets)
        int anotherU16Value = Short.toUnsignedInt(buffer.getShort(3));
        assertEquals(0x6789, anotherU16Value);
        
        // Lire S32 (4 octets)
        int s32Value = buffer.getInt(5);
        assertEquals(0xffffffffL, s32Value & 0xffffffffL);
        assertEquals(-1, s32Value); // Valeur signée
    }

    @Test
    void extractTripPosIdTest() {
        // Ce test montre comment extraire l'ID de course et la position à partir d'un TRIP_POS_ID
        
        // 0x0000 0550 = trip ID 5, position 0x50 (80 en décimal)
        byte[] bytes = HexFormat.ofDelimiter(" ").parseHex("00 00 05 50");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        
        int tripPosId = buffer.getInt(0);
        
        // Utiliser Bits32_24_8 pour extraire les composantes
        int tripId = Bits32_24_8.unpack24(tripPosId);
        int position = Bits32_24_8.unpack8(tripPosId);
        
        assertEquals(5, tripId);
        assertEquals(80, position);
    }

    /**
     * Classe fictive Bits32_24_8 pour les besoins du test.
     * Si vous exécutez ce test, assurez-vous d'utiliser votre propre implémentation.
     */
    private static class Bits32_24_8 {
        public static int unpack24(int bits32) {
            return (bits32 >> 8) & 0xFFFFFF;
        }
        
        public static int unpack8(int bits32) {
            return bits32 & 0xFF;
        }
    }

    /**
     * Classe fictive PackedRange pour les besoins du test.
     * Si vous exécutez ce test, assurez-vous d'utiliser votre propre implémentation.
     */
    private static class PackedRange {
        public static int startInclusive(int interval) {
            return Bits32_24_8.unpack24(interval);
        }
        
        public static int endExclusive(int interval) {
            return startInclusive(interval) + length(interval);
        }
        
        public static int length(int interval) {
            return Bits32_24_8.unpack8(interval);
        }
    }
}
