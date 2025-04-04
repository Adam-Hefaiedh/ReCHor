package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferedTripsTest {
    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }

    private static final ByteBuffer TRIPS =
            byteBuffer("00 02 00 00 00 01 00 02 00 00 00 01");

    private static final List<String> STRING_TABLE = List.of(
            "Lausanne",
            "Luzern",
            "Rorschach",
            "m1",
            "IR 15",
            "IC 1");

    @Test
    void bufferedTripsConstructorAcceptsEmptyBuffer() {
        assertDoesNotThrow(() -> {
            new BufferedTrips(List.of(), ByteBuffer.allocate(0));
        });
    }

    @Test
    void bufferedTripsSizeWorks() {
        assertEquals(3, new BufferedTrips(STRING_TABLE, TRIPS).size());
    }

    @Test
    void bufferedTripsRouteIdWorks() {
        var t = new BufferedTrips(STRING_TABLE, TRIPS);

        assertEquals(2, t.routeId(0));
        assertEquals(1, t.routeId(1));
        assertEquals(0, t.routeId(2));
    }

    @Test
    void bufferedTripsDestinationWorks() {
        var t = new BufferedTrips(STRING_TABLE, TRIPS);

        assertEquals("Lausanne", t.destination(0));
        assertEquals("Rorschach", t.destination(1));
        assertEquals("Luzern", t.destination(2));
    }
}