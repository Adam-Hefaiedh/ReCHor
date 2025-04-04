package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferedTransfersTest {
    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }


    private static final List<Integer> ARR_STATIONS = List.of(1, 0, 2, 4, 3);
    private static final int STATIONS_COUNT = ARR_STATIONS.size();

    private static final ByteBuffer TRANSFERS = byteBuffer(
            "00 00 00 01 02 " +
            "00 01 00 01 01 " +
            "00 02 00 01 02 " +
            "00 03 00 01 03 " +
            "00 04 00 01 04 " +
            "00 00 00 00 01 " +
            "00 01 00 00 02 " +
            "00 02 00 00 03 " +
            "00 03 00 00 04 " +
            "00 04 00 00 05 " +
            "00 00 00 02 03 " +
            "00 01 00 02 02 " +
            "00 02 00 02 01 " +
            "00 03 00 02 02 " +
            "00 04 00 02 03 " +
            "00 00 00 04 05 " +
            "00 01 00 04 04 " +
            "00 02 00 04 03 " +
            "00 03 00 04 02 " +
            "00 04 00 04 01 " +
            "00 00 00 03 04 " +
            "00 01 00 03 03 " +
            "00 02 00 03 02 " +
            "00 03 00 03 01 " +
            "00 04 00 03 02");

    @Test
    void bufferedTransfersConstructorAcceptsEmptyBuffer() {
        assertDoesNotThrow(() -> {
            new BufferedTransfers(ByteBuffer.allocate(0));
        });
    }

    @Test
    void bufferedTransfersSizeWorks() {
        assertEquals(STATIONS_COUNT * STATIONS_COUNT, new BufferedTransfers(TRANSFERS).size());
    }

    @Test
    void bufferedTransfersDepStationIdWorks() {
        var t = new BufferedTransfers(TRANSFERS);

        var i = 0;
        for (var ignored : ARR_STATIONS) {
            for (var expDepStationId = 0; expDepStationId < STATIONS_COUNT; expDepStationId += 1) {
                assertEquals(expDepStationId, t.depStationId(i++));
            }
        }
    }

    @Test
    void bufferedTransfersMinutesWorks() {
        var t = new BufferedTransfers(TRANSFERS);

        var i = 0;
        for (var arrStationId : ARR_STATIONS) {
            for (var depStationId = 0; depStationId < STATIONS_COUNT; depStationId += 1) {
                var expMinutes = 1 + Math.abs(arrStationId - depStationId);
                assertEquals(expMinutes, t.minutes(i++));
            }
        }
    }

    @Test
    void bufferedTransfersArrivingAtWorks() {
        var t = new BufferedTransfers(TRANSFERS);

        for (var i = 0; i < STATIONS_COUNT; i += 1) {
            var expArrivingAt = PackedRange.pack(i * STATIONS_COUNT, (i + 1) * STATIONS_COUNT);
            assertEquals(expArrivingAt, t.arrivingAt(ARR_STATIONS.get(i)));
        }
    }

    @Test
    void bufferedTransfersMinutesBetweenWorks() {
        var t = new BufferedTransfers(TRANSFERS);

        for (int depStationId = 0; depStationId < STATIONS_COUNT; depStationId += 1) {
            for (int arrStationId = 0; arrStationId < STATIONS_COUNT; arrStationId += 1) {
                var expMinutes = 1 + Math.abs(depStationId - arrStationId);
                assertEquals(expMinutes, t.minutesBetween(depStationId, arrStationId));
            }
        }
    }

    @Test
    void bufferedTransferMinutesBetweenThrowsWithInvalidDepStation() {
        var t = new BufferedTransfers(TRANSFERS);
        assertThrows(RuntimeException.class, () -> {
            t.minutesBetween(STATIONS_COUNT, 0);
        });
    }
}