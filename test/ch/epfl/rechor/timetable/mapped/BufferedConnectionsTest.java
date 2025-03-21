package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class BufferedConnectionsTest {

    // Données de liaisons
    // Format: DEP_STOP_ID (U16) | DEP_MINUTES (U16) | ARR_STOP_ID (U16) | ARR_MINUTES (U16) | TRIP_POS_ID (S32)
    private static final byte[] CONNECTIONS_BYTES = {
            // Liaison 0: départ arrêt 10 à 540 min (9h00), arrivée arrêt 20 à 555 min (9h15), course 5 position 0
            0x00, 0x0A, 0x02, 0x1C, 0x00, 0x14, 0x02, 0x2B, 0x00, 0x00, 0x05, 0x00,
            // Liaison 1: départ arrêt 20 à 560 min (9h20), arrivée arrêt 30 à 580 min (9h40), course 5 position 1
            0x00, 0x14, 0x02, 0x30, 0x00, 0x1E, 0x02, 0x44, 0x00, 0x00, 0x05, 0x01,
            // Liaison 2: départ arrêt 30 à 480 min (8h00), arrivée arrêt 40 à 495 min (8h15), course 10 position 0
            0x00, 0x1E, 0x01, (byte) 0xE0, 0x00, 0x28, 0x01, (byte) 0xEF, 0x00, 0x00, 0x0A, 0x00
    };

    // Données des liaisons suivantes
    // Format: NEXT_CONNECTION_ID (S32)
    private static final byte[] SUCC_BYTES = {
            0x00, 0x00, 0x00, 0x01,  // L'index de la liaison suivant liaison 0 est 1
            0x00, 0x00, 0x00, 0x00,  // L'index de la liaison suivant liaison 1 est 0 (fin de course circulaire)
            0x00, 0x00, 0x00, 0x02   // L'index de la liaison suivant liaison 2 est 2 (lui-même - seule liaison dans la course)
    };

    @Test
    void sizeReturnsCorrectNumberOfConnections() {
        ByteBuffer buffer = ByteBuffer.wrap(CONNECTIONS_BYTES);
        ByteBuffer succBuffer = ByteBuffer.wrap(SUCC_BYTES);
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        assertEquals(3, connections.size());
    }

    @Test
    void depStopIdReturnsCorrectDepartureStopId() {
        ByteBuffer buffer = ByteBuffer.wrap(CONNECTIONS_BYTES);
        ByteBuffer succBuffer = ByteBuffer.wrap(SUCC_BYTES);
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        assertEquals(10, connections.depStopId(0));
        assertEquals(20, connections.depStopId(1));
        assertEquals(30, connections.depStopId(2));
    }

    @Test
    void depMinsReturnsCorrectDepartureTime() {
        ByteBuffer buffer = ByteBuffer.wrap(CONNECTIONS_BYTES);
        ByteBuffer succBuffer = ByteBuffer.wrap(SUCC_BYTES);
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        assertEquals(540, connections.depMins(0)); // 9h00
        assertEquals(560, connections.depMins(1)); // 9h20
        assertEquals(480, connections.depMins(2)); // 8h00
    }

    @Test
    void arrStopIdReturnsCorrectArrivalStopId() {
        ByteBuffer buffer = ByteBuffer.wrap(CONNECTIONS_BYTES);
        ByteBuffer succBuffer = ByteBuffer.wrap(SUCC_BYTES);
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        assertEquals(20, connections.arrStopId(0));
        assertEquals(30, connections.arrStopId(1));
        assertEquals(40, connections.arrStopId(2));
    }

    @Test
    void arrMinsReturnsCorrectArrivalTime() {
        ByteBuffer buffer = ByteBuffer.wrap(CONNECTIONS_BYTES);
        ByteBuffer succBuffer = ByteBuffer.wrap(SUCC_BYTES);
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        assertEquals(555, connections.arrMins(0)); // 9h15
        assertEquals(580, connections.arrMins(1)); // 9h40
        assertEquals(495, connections.arrMins(2)); // 8h15
    }

    @Test
    void tripIdReturnsCorrectTripId() {
        ByteBuffer buffer = ByteBuffer.wrap(CONNECTIONS_BYTES);
        ByteBuffer succBuffer = ByteBuffer.wrap(SUCC_BYTES);
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        assertEquals(5, connections.tripId(0));
        assertEquals(5, connections.tripId(1));
        assertEquals(10, connections.tripId(2));
    }

    @Test
    void tripPosReturnsCorrectPositionInTrip() {
        ByteBuffer buffer = ByteBuffer.wrap(CONNECTIONS_BYTES);
        ByteBuffer succBuffer = ByteBuffer.wrap(SUCC_BYTES);
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        assertEquals(0, connections.tripPos(0));
        assertEquals(1, connections.tripPos(1));
        assertEquals(0, connections.tripPos(2));
    }

    @Test
    void nextConnectionIdReturnsCorrectNextConnectionId() {
        ByteBuffer buffer = ByteBuffer.wrap(CONNECTIONS_BYTES);
        ByteBuffer succBuffer = ByteBuffer.wrap(SUCC_BYTES);
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        assertEquals(1, connections.nextConnectionId(0));
        assertEquals(0, connections.nextConnectionId(1)); // Retour à la première liaison de la course
        assertEquals(2, connections.nextConnectionId(2)); // Seule liaison dans sa course, pointe vers elle-même
    }

    @Test
    void throwsExceptionForInvalidIndex() {
        ByteBuffer buffer = ByteBuffer.wrap(CONNECTIONS_BYTES);
        ByteBuffer succBuffer = ByteBuffer.wrap(SUCC_BYTES);
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        assertThrows(IndexOutOfBoundsException.class, () -> connections.depStopId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> connections.depStopId(3));
    }
}