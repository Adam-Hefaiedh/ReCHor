package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class BufferedTransfersTest {

    // Exemple de données de changements
    // Les données sont ordonnées pour que les changements arrivant à la même gare soient consécutifs
    // Format: DEP_STATION_ID (U16) | ARR_STATION_ID (U16) | TRANSFER_MINUTES (U8)
    private static final byte[] TRANSFERS_BYTES = {
            // Gare d'arrivée 1
            0x00, 0x02, 0x00, 0x01, 0x05,  // Changement de la gare 2 vers gare 1, durée 5 min
            0x00, 0x03, 0x00, 0x01, 0x08,  // Changement de la gare 3 vers gare 1, durée 8 min
            
            // Gare d'arrivée 2
            0x00, 0x01, 0x00, 0x02, 0x04,  // Changement de la gare 1 vers gare 2, durée 4 min
            0x00, 0x04, 0x00, 0x02, 0x06,  // Changement de la gare 4 vers gare 2, durée 6 min
            
            // Gare d'arrivée 3
            0x00, 0x03, 0x00, 0x03, 0x03,  // Changement au sein de la gare 3, durée 3 min
            0x00, 0x04, 0x00, 0x03, 0x09   // Changement de la gare 4 vers gare 3, durée 9 min
    };

    @Test
    void sizeReturnsCorrectNumberOfTransfers() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);
        
        assertEquals(6, transfers.size());
    }

    @Test
    void depStationIdReturnsCorrectDepartureStationId() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);
        
        assertEquals(2, transfers.depStationId(0));
        assertEquals(3, transfers.depStationId(1));
        assertEquals(1, transfers.depStationId(2));
        assertEquals(4, transfers.depStationId(3));
        assertEquals(3, transfers.depStationId(4));
        assertEquals(4, transfers.depStationId(5));
    }

    @Test
    void minutesReturnsCorrectTransferDuration() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);
        
        assertEquals(5, transfers.minutes(0));
        assertEquals(8, transfers.minutes(1));
        assertEquals(4, transfers.minutes(2));
        assertEquals(6, transfers.minutes(3));
        assertEquals(3, transfers.minutes(4));
        assertEquals(9, transfers.minutes(5));
    }

    @Test
    void arrivingAtReturnsCorrectTransferInterval() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);
        
        // Test pour la gare d'arrivée 1 (les deux premiers changements)
        int stationId = 1;
        int interval = transfers.arrivingAt(stationId);
        
        // Vérifier que l'intervalle contient bien les index 0 et 1
        Assertions.assertEquals(0, PackedRange.startInclusive(interval));
        assertEquals(2, PackedRange.endExclusive(interval));
        
        // Tester pour gare d'arrivée 2 (changements 2 et 3)
        stationId = 2;
        interval = transfers.arrivingAt(stationId);
        assertEquals(2, PackedRange.startInclusive(interval));
        assertEquals(4, PackedRange.endExclusive(interval));
        
        // Tester pour gare d'arrivée 3 (changements 4 et 5)
        stationId = 3;
        interval = transfers.arrivingAt(stationId);
        assertEquals(4, PackedRange.startInclusive(interval));
        assertEquals(6, PackedRange.endExclusive(interval));
        

    }

    @Test
    void minutesBetweenReturnsCorrectTransferTime() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);
        
        // Test de changements existants
        assertEquals(5, transfers.minutesBetween(2, 1));
        assertEquals(8, transfers.minutesBetween(3, 1));
        assertEquals(4, transfers.minutesBetween(1, 2));
        assertEquals(6, transfers.minutesBetween(4, 2));
        assertEquals(3, transfers.minutesBetween(3, 3)); // Changement interne à une gare
        assertEquals(9, transfers.minutesBetween(4, 3));
    }

    @Test
    void minutesBetweenThrowsExceptionForNonExistingTransfer() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);
        
        // Test de changements qui n'existent pas
        assertThrows(NoSuchElementException.class, () -> transfers.minutesBetween(1, 3));
    }

    @Test
    void throwsExceptionForInvalidIndex() {
        ByteBuffer buffer = ByteBuffer.wrap(TRANSFERS_BYTES);
        BufferedTransfers transfers = new BufferedTransfers(buffer);
        
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(6));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.minutes(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.minutes(6));
    }
}
