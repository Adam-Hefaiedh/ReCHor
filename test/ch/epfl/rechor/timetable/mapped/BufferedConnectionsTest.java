package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BufferedConnectionsTest {
    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }

    private static final ByteBuffer CONNECTIONS =
            byteBuffer("00 00 00 00 00 01 00 01 00 00 00 ff" +
                       " 00 0a 02 62 00 0b 02 9e 00 00 0a 01" +
                       " 03 e8 06 17 03 e7 06 53 ff ff ff 0a");

    private static final ByteBuffer CONNECTIONS_SUCCS =
            byteBuffer("00 00 00 02 00 00 00 00 00 00 00 01");

    @Test
    void bufferedConnectionsConstructorAcceptsEmptyBuffer() {
        assertDoesNotThrow(() -> {
            new BufferedConnections(ByteBuffer.allocate(0), ByteBuffer.allocate(0));
        });
    }

    @Test
    void bufferedConnectionsSizeWorks() {
        assertEquals(3, new BufferedConnections(CONNECTIONS, CONNECTIONS_SUCCS).size());
    }

    @Test
    void bufferedConnectionsDepStopIdWorks() {
        var c = new BufferedConnections(CONNECTIONS, CONNECTIONS_SUCCS);

        assertEquals(0, c.depStopId(0));
        assertEquals(10, c.depStopId(1));
        assertEquals(1000, c.depStopId(2));
    }

    private static int minutes(int h, int m) {
        return h * 60 + m;
    }

    @Test
    void bufferedConnectionsDepMinsWorks() {
        var c = new BufferedConnections(CONNECTIONS, CONNECTIONS_SUCCS);

        assertEquals(minutes(0, 0), c.depMins(0));
        assertEquals(minutes(10, 10), c.depMins(1));
        assertEquals(minutes(25, 59), c.depMins(2));
    }

    @Test
    void bufferedConnectionsArrStopIdWorks() {
        var c = new BufferedConnections(CONNECTIONS, CONNECTIONS_SUCCS);

        assertEquals(1, c.arrStopId(0));
        assertEquals(11, c.arrStopId(1));
        assertEquals(999, c.arrStopId(2));
    }

    @Test
    void bufferedConnectionsArrMinsWorks() {
        var c = new BufferedConnections(CONNECTIONS, CONNECTIONS_SUCCS);

        assertEquals(minutes(0, 1), c.arrMins(0));
        assertEquals(minutes(11, 10), c.arrMins(1));
        assertEquals(minutes(26, 59), c.arrMins(2));
    }

    @Test
    void bufferedConnectionsTripIdWorks() {
        var c = new BufferedConnections(CONNECTIONS, CONNECTIONS_SUCCS);

        assertEquals(0, c.tripId(0));
        assertEquals(10, c.tripId(1));
        assertEquals(0xFFFFFF, c.tripId(2));
    }

    @Test
    void bufferedConnectionsTripPosWorks() {
        var c = new BufferedConnections(CONNECTIONS, CONNECTIONS_SUCCS);

        assertEquals(0xFF, c.tripPos(0));
        assertEquals(1, c.tripPos(1));
        assertEquals(10, c.tripPos(2));
    }

    @Test
    void bufferedConnectionsNextConnectionIdWorks() {
        var c = new BufferedConnections(CONNECTIONS, CONNECTIONS_SUCCS);

        assertEquals(2, c.nextConnectionId(0));
        assertEquals(0, c.nextConnectionId(1));
        assertEquals(1, c.nextConnectionId(2));
    }
}