package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferedRoutesTest {

    // Exemple de données de routes
    // Format: NAME_ID (U16) | KIND (U8)
    private static final byte[] ROUTES_BYTES = {
            0x00, 0x05, 0x01,  // Ligne "Losanna" (index 5), METRO (1)
            0x00, 0x06, 0x00,  // Ligne "Palézieux" (index 6), TRAM (0)
            0x00, 0x03, 0x02   // Ligne "Ins" (index 3), TRAIN (2)
    };

    private static final List<String> STRING_TABLE = List.of(
            "1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux"
    );

    @Test
    void sizeReturnsCorrectNumberOfRoutes() {
        ByteBuffer buffer = ByteBuffer.wrap(ROUTES_BYTES);
        BufferedRoutes routes = new BufferedRoutes(STRING_TABLE, buffer);
        
        assertEquals(3, routes.size());
    }

    @Test
    void nameReturnsCorrectRouteName() {
        ByteBuffer buffer = ByteBuffer.wrap(ROUTES_BYTES);
        BufferedRoutes routes = new BufferedRoutes(STRING_TABLE, buffer);
        
        assertEquals("Losanna", routes.name(0));
        assertEquals("Palézieux", routes.name(1));
        assertEquals("Ins", routes.name(2));
    }

    @Test
    void vehicleReturnsCorrectVehicleType() {
        ByteBuffer buffer = ByteBuffer.wrap(ROUTES_BYTES);
        BufferedRoutes routes = new BufferedRoutes(STRING_TABLE, buffer);
        
        assertEquals(Vehicle.METRO, routes.vehicle(0));
        assertEquals(Vehicle.TRAM, routes.vehicle(1));
        assertEquals(Vehicle.TRAIN, routes.vehicle(2));
    }

    @Test
    void throwsExceptionForInvalidIndex() {
        ByteBuffer buffer = ByteBuffer.wrap(ROUTES_BYTES);
        BufferedRoutes routes = new BufferedRoutes(STRING_TABLE, buffer);
        
        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.name(3));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> routes.vehicle(3));
    }
}
