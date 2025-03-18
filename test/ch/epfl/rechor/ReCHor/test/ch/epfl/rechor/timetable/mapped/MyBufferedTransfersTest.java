package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyBufferedTransfersTest {

    // According to the documentation structure:
    // - DEP_STATION_ID (U16): Index of the departure station
    // - ARR_STATION_ID (U16): Index of the arrival station
    // - TRANSFER_MINUTES (U8): Duration of the transfer in minutes

    @Test
    void constructorRejectsInvalidBuffer() {
        // Buffer should be a multiple of 5 bytes
        ByteBuffer invalidBuffer = ByteBuffer.allocate(7); // Not divisible by 5

        assertThrows(IllegalArgumentException.class, () ->
                new BufferedTransfers(invalidBuffer));
    }

    @Test
    void transfersAreAccessedCorrectly() {
        // Create raw bytes for transfers
        // The table is organized so that all transfers arriving at the same station are consecutive
        // Arriving at station 2: transfers 0 and 1
        // Arriving at station 4: transfers 2 and 3
        // Arriving at station 5: transfer 4
        ByteBuffer buffer = ByteBuffer.allocate(25); // 5 transfers * 5 bytes

        // Transfer 0: From station 1 to station 2, 5 minutes
        buffer.putShort((short)1); // DEP_STATION_ID
        buffer.putShort((short)2); // ARR_STATION_ID
        buffer.put((byte)5);      // TRANSFER_MINUTES

        // Transfer 1: From station 3 to station 2, 8 minutes
        buffer.putShort((short)3); // DEP_STATION_ID
        buffer.putShort((short)2); // ARR_STATION_ID
        buffer.put((byte)8);      // TRANSFER_MINUTES

        // Transfer 2: From station 1 to station 4, 10 minutes
        buffer.putShort((short)1); // DEP_STATION_ID
        buffer.putShort((short)4); // ARR_STATION_ID
        buffer.put((byte)10);     // TRANSFER_MINUTES

        // Transfer 3: From station 2 to station 4, 7 minutes
        buffer.putShort((short)2); // DEP_STATION_ID
        buffer.putShort((short)4); // ARR_STATION_ID
        buffer.put((byte)7);      // TRANSFER_MINUTES

        // Transfer 4: From station 3 to station 5, 3 minutes
        buffer.putShort((short)3); // DEP_STATION_ID
        buffer.putShort((short)5); // ARR_STATION_ID
        buffer.put((byte)3);      // TRANSFER_MINUTES

        buffer.flip(); // Reset position to beginning

        // Create the BufferedTransfers instance
        BufferedTransfers transfers = new BufferedTransfers(buffer);

        // Test size
        assertEquals(5, transfers.size());

        // Test transfer properties
        assertEquals(1, transfers.depStationId(0));
        assertEquals(5, transfers.minutes(0));

        assertEquals(3, transfers.depStationId(1));
        assertEquals(8, transfers.minutes(1));

        assertEquals(1, transfers.depStationId(2));
        assertEquals(10, transfers.minutes(2));

        // Test arrivingAt method
        // For station 2, should return interval [0, 2) - transfers 0 and 1
        int station2Interval = transfers.arrivingAt(2);
        assertEquals(0, PackedRange.startInclusive(station2Interval));
        assertEquals(2, PackedRange.endExclusive(station2Interval));

        // For station 4, should return interval [2, 4) - transfers 2 and 3
        int station4Interval = transfers.arrivingAt(4);
        assertEquals(2, PackedRange.startInclusive(station4Interval));
        assertEquals(4, PackedRange.endExclusive(station4Interval));

        // For station 5, should return interval [4, 5) - only transfer 4
        int station5Interval = transfers.arrivingAt(5);
        assertEquals(4, PackedRange.startInclusive(station5Interval));
        assertEquals(5, PackedRange.endExclusive(station5Interval));

        // For a station with no arriving transfers
        int station9Interval = transfers.arrivingAt(9);
        assertEquals(0, PackedRange.length(station9Interval)); // Empty interval

        // Test minutesBetween method
        assertEquals(5, transfers.minutesBetween(1, 2)); // Transfer 0
        assertEquals(8, transfers.minutesBetween(3, 2)); // Transfer 1
        assertEquals(10, transfers.minutesBetween(1, 4)); // Transfer 2
        assertEquals(7, transfers.minutesBetween(2, 4)); // Transfer 3
        assertEquals(3, transfers.minutesBetween(3, 5)); // Transfer 4

        // Test for non-existent transfer
        assertThrows(NoSuchElementException.class, () -> transfers.minutesBetween(1, 3));

        // Test invalid indices
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(5));
    }
}