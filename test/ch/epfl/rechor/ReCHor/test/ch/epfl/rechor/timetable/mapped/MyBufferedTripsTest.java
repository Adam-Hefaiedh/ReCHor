package ch.epfl.rechor.ReCHor.test.ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedTrips;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyBufferedTripsTest {

    // According to the documentation structure:
    // - ROUTE_ID (U16): Index of the route for this trip
    // - DESTINATION_ID (U16): String index of the final destination

    @Test
    void constructorRejectsInvalidBuffer() {
        List<String> strings = List.of("Lausanne", "Genève");
        // Create a buffer that isn't a multiple of the structure size (4 bytes)
        ByteBuffer invalidBuffer = ByteBuffer.allocate(6); // Not divisible by 4

        assertThrows(IllegalArgumentException.class, () ->
                new BufferedTrips(strings, invalidBuffer));
    }

    @Test
    void tripsAreAccessedCorrectly() {
        // Setup the string table
        List<String> strings = List.of("Lausanne", "Genève", "Bern");

        // Create raw bytes for three trips:
        // 1. Route index 5, destination "Lausanne"
        // 2. Route index 10, destination "Genève"
        // 3. Route index 7, destination "Bern"
        ByteBuffer buffer = ByteBuffer.allocate(12); // 3 trips * 4 bytes

        // Trip 1
        buffer.putShort((short)5);  // ROUTE_ID = 5
        buffer.putShort((short)0);  // DESTINATION_ID = 0 ("Lausanne")

        // Trip 2
        buffer.putShort((short)10); // ROUTE_ID = 10
        buffer.putShort((short)1);  // DESTINATION_ID = 1 ("Genève")

        // Trip 3
        buffer.putShort((short)7);  // ROUTE_ID = 7
        buffer.putShort((short)2);  // DESTINATION_ID = 2 ("Bern")

        buffer.flip(); // Reset position to beginning

        // Create the BufferedTrips instance
        BufferedTrips trips = new BufferedTrips(strings, buffer);

        // Test size
        assertEquals(3, trips.size());

        // Test route IDs
        assertEquals(5, trips.routeId(0));
        assertEquals(10, trips.routeId(1));
        assertEquals(7, trips.routeId(2));

        // Test destinations
        assertEquals("Lausanne", trips.destination(0));
        assertEquals("Genève", trips.destination(1));
        assertEquals("Bern", trips.destination(2));

        // Test invalid indices
        assertThrows(IndexOutOfBoundsException.class, () -> trips.routeId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.routeId(3));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.destination(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> trips.destination(3));
    }
}