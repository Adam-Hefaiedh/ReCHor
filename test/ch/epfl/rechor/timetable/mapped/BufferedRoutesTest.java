package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferedRoutesTest {
    private static ByteBuffer byteBuffer(String hex) {
        return ByteBuffer
                .wrap(HexFormat.ofDelimiter(" ").parseHex(hex))
                .asReadOnlyBuffer();
    }

    private static final ByteBuffer ROUTES =
            byteBuffer("00 03 00 00 04 06 00 05 03");

    private static final List<String> STRING_TABLE = List.of(
            "Lausanne",
            "Luzern",
            "Rorschach",
            "m1",
            "IR 15",
            "IC 1");

    @Test
    void bufferedRoutesConstructorAcceptsEmptyBuffer() {
        assertDoesNotThrow(() -> {
            new BufferedRoutes(List.of(), ByteBuffer.allocate(0));
        });
    }

    @Test
    void bufferedRoutesSizeWorks() {
        assertEquals(3, new BufferedRoutes(STRING_TABLE, ROUTES).size());
    }

    @Test
    void bufferedRoutesNameWorks() {
        var r = new BufferedRoutes(STRING_TABLE, ROUTES);

        assertEquals("m1", r.name(0));
        assertEquals("IR 15", r.name(1));
        assertEquals("IC 1", r.name(2));
    }

    @Test
    void bufferedRoutesVehicleWorks() {
        var r = new BufferedRoutes(STRING_TABLE, ROUTES);

        assertEquals(Vehicle.TRAM, r.vehicle(0));
        assertEquals(Vehicle.FUNICULAR, r.vehicle(1));
        assertEquals(Vehicle.BUS, r.vehicle(2));
    }
}