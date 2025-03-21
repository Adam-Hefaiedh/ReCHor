package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BufferedTripsTest {

    // Exemple de données de courses (trips)
    // Format: ROUTE_ID (U16) | DESTINATION_ID (U16)
    private static final byte[] TRIPS_BYTES = {
            0x00, 0x01, 0x00, 0x04,  // Route d'index 1, destination "Lausanne" (index 4)
            0x00, 0x02, 0x00, 0x02,  // Route d'index 2, destination "Anet" (index 2)
            0x00, 0x00, 0x00, 0x06   // Route d'index 0, destination "Palézieux" (index 6)
    };

    private static final List<String> STRING_TABLE = List.of(
            "1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux"
    );

    @Test
    void sizeReturnsCorrectNumberOfTrips() {
        ByteBuffer buffer = ByteBuffer.wrap(TRIPS_BYTES);
        BufferedTrips trips = new BufferedTrips(STRING_TABLE, buffer);
        
        assertEquals(3, trips.size());
    }

    @Test
    void routeIdReturnsCorrectRouteIndex() {
        ByteBuffer buffer = ByteBuffer.wrap(TRIPS_BYTES);
        BufferedTrips trips = new BufferedTrips(STRING_TABLE, buffer);
        
        assertEquals(1, trips.routeId(0));
        assertEquals(2, trips.routeId(1));
        assertEquals(0, trips.routeId(2));
    }

    @Test
    void destinationReturnsCorrectDestinationName() {
        ByteBuffer buffer = ByteBuffer.wrap(TRIPS_BYTES);
        BufferedTrips trips = new BufferedTrips(STRING_TABLE, buffer);
        
        assertEquals("Lausanne", trips.destination(0));
        assertEquals("Anet", trips.destination(1));
        assertEquals("Palézieux", trips.destination(2));
    }

    @Test
    void throwsExceptionForInvalidIndex() {
        ByteBuffer buffer = ByteBuffer.wrap(TRIPS_BYTES);
        BufferedTrips trips = new BufferedTrips(STRING_TABLE, buffer);
        
        assertThrows(IndexOutOfBoundsException.class, () -> trips.routeId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.routeId(3));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.destination(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.destination(3));
    }
}
