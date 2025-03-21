package ch.epfl.rechor.timetable.mapped;


import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.journey.PackedCriteria;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.nio.ByteBuffer;
import java.util.HexFormat;

public class TransferTest {
    private static final HexFormat HEX = HexFormat.ofDelimiter(" ");
    private static final ByteBuffer BUFFER = ByteBuffer.wrap(HEX.parseHex(
            "00 01 00 02 05 " +          // Changement de 1 → 2 en 5 minutes
                    "00 03 00 02 0A " +  // Changement de 3 → 2 en 10 minutes
                    "00 04 00 02 0F " +  // Changement de 4 → 2 en 15 minutes
                    "00 02 00 05 14"    // Changement de 2 → 5 en 20 minutes
    ));


    private final BufferedTransfers transfers = new BufferedTransfers(BUFFER);

    @Test
    void sizeReturnsCorrectValue() {
        assertEquals(4, transfers.size());
    }


    @Test
    void depStationIdReturnsCorrectValues() {
        assertEquals(1, transfers.depStationId(0));
        assertEquals(3, transfers.depStationId(1));
        assertEquals(4, transfers.depStationId(2));
        assertEquals(2, transfers.depStationId(3));
    }

    @Test
    void arrivingAtReturnsCorrectInterval() {
        int interval = transfers.arrivingAt(2);
        assertEquals(0, interval >>> 16); // Début
        assertEquals(2, interval & 0xFFFF); // Fin

        int interval2 = transfers.arrivingAt(5);

        assertEquals(3, interval2 >>> 16); // Début
        assertEquals(3, interval2 & 0xFFFF); // Fin
    }

    @Test
    void arrivingAtThrowsForInvalidStationId() {
        assertThrows(IllegalArgumentException.class, () -> transfers.arrivingAt(-1));
        assertThrows(IllegalArgumentException.class, () -> transfers.arrivingAt(9999));
    }

    @Test
    void minutesReturnsCorrectValues() {
        assertEquals(5, transfers.minutes(0));
        assertEquals(10, transfers.minutes(1));
        assertEquals(15, transfers.minutes(2));
        assertEquals(20, transfers.minutes(3));
    }

    @Test
    void minutesBetweenReturnsCorrectValue() {
        assertEquals(5, transfers.minutesBetween(1, 2));
        assertEquals(10, transfers.minutesBetween(3, 2));
        assertEquals(15, transfers.minutesBetween(4, 2));
        assertEquals(20, transfers.minutesBetween(2, 5));
        assertThrows(IllegalArgumentException.class, () -> transfers.minutesBetween(4, 5));


    }


    @Test
    void depStationIdThrowsForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.depStationId(999));
    }



    @Test
    void minutesThrowsForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.minutes(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> transfers.minutes(999));
    }


    @Test
    void minutesBetweenHandlesInvalidCases() {
        assertThrows(IllegalArgumentException.class, () -> transfers.minutesBetween(99, 2));
        assertThrows(IllegalArgumentException.class, () -> transfers.minutesBetween(1, 99));
    }


    @Test
    void constructorHandlesEmptyBuffer() {
        ByteBuffer emptyBuffer = ByteBuffer.allocate(0);
        BufferedTransfers emptyTransfers = new BufferedTransfers(emptyBuffer);
        assertEquals(0, emptyTransfers.size());
    }


    @Test
    void constructorRejectsInvalidBufferSize() {
        ByteBuffer invalidBuffer = ByteBuffer.allocate(7);
        assertThrows(IllegalArgumentException.class, () -> new BufferedTransfers(invalidBuffer));
    }


    @Test
    void arrivingAtHandlesMultipleTransfersToSameStation() {
        ByteBuffer buffer = ByteBuffer.wrap(HEX.parseHex(
                "00 01 00 02 05 " +
                        "00 03 00 02 10 " +
                        "00 04 00 02 15 " +
                        "00 05 00 02 20 " +
                        "00 06 00 03 25"
        ));
        BufferedTransfers multipleTransfers = new BufferedTransfers(buffer);


        int interval = multipleTransfers.arrivingAt(2);
        assertEquals(0, interval >>> 16);
        assertEquals(3, interval & 0xFFFF);


        int interval3 = multipleTransfers.arrivingAt(3);
        assertEquals(4, interval3 >>> 16);
        assertEquals(4, interval3 & 0xFFFF);
    }


    @Test
    void transfersAllowCircularConnections() {
        ByteBuffer buffer = ByteBuffer.wrap(HEX.parseHex(
                "00 01 00 01 07 " +
                        "00 02 00 02 05"
        ));
        BufferedTransfers circularTransfers = new BufferedTransfers(buffer);


        assertEquals(7, circularTransfers.minutesBetween(1, 1));
        assertEquals(5, circularTransfers.minutesBetween(2, 2));
    }


    @Test
    void allTransfersGoToSameStation() {
        ByteBuffer buffer = ByteBuffer.wrap(HEX.parseHex(
                "00 01 00 05 03 " +
                        "00 02 00 05 06 " +
                        "00 03 00 05 09 " +
                        "00 04 00 05 12"
        ));
        BufferedTransfers allToSame = new BufferedTransfers(buffer);


        int interval = allToSame.arrivingAt(5);
        assertEquals(0, interval >>> 16);
        assertEquals(3, interval);
    }


    @Test
    void transfersHandleExtremeValues() {
        ByteBuffer buffer = ByteBuffer.wrap(HEX.parseHex(
                "00 01 00 02 00 " +
                        "00 02 00 03 FF"
        ));
        BufferedTransfers extremeTransfers = new BufferedTransfers(buffer);


        assertEquals(0, extremeTransfers.minutesBetween(1, 2));
        assertEquals(255, extremeTransfers.minutesBetween(2, 3));
    }


    @Test
    void constructorRejectsInvalidBuffers() {
        ByteBuffer tooSmallBuffer = ByteBuffer.wrap(HEX.parseHex("00 01"));
        ByteBuffer misalignedBuffer = ByteBuffer.wrap(HEX.parseHex("00 01 00 02 05 00"));


        assertThrows(IllegalArgumentException.class, () -> new BufferedTransfers(tooSmallBuffer));
        assertThrows(IllegalArgumentException.class, () -> new BufferedTransfers(misalignedBuffer));
    }


    @Test
    void allTransfersGoToSameStation2() {
        ByteBuffer buffer = ByteBuffer.wrap(HEX.parseHex(
                "00 01 00 05 03 " +
                        "00 02 00 05 06 " +
                        "00 03 00 05 09 " +
                        "00 04 00 05 12"
        ));
        BufferedTransfers allToSame = new BufferedTransfers(buffer);


        int interval = allToSame.arrivingAt(5);
        assertEquals(0, interval >>> 16);
        assertEquals(3, interval & 0xFFFF);
    }


    @Test
    void constructorRejectsInvalidBufferSize2() {
        ByteBuffer invalidBuffer = ByteBuffer.allocate(7);
        assertThrows(IllegalArgumentException.class, () -> new BufferedTransfers(invalidBuffer));
    }


    @Test
    void constructorHandlesEmptyBuffer2() {
        ByteBuffer emptyBuffer = ByteBuffer.allocate(0);
        BufferedTransfers emptyTransfers = new BufferedTransfers(emptyBuffer);
        assertEquals(0, emptyTransfers.size());
    }


    @Test
    void arrivingAtThrowsForInvalidStationId2() {
        assertThrows(IllegalArgumentException.class, () -> transfers.arrivingAt(-1));
        assertThrows(IllegalArgumentException.class, () -> transfers.arrivingAt(9999));
    }
}