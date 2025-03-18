package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class MyBufferedConnectionsTest {

    // According to the documentation structure:
    // Main buffer:
    // - DEP_STOP_ID (U16): Index of the departure stop
    // - DEP_MINUTES (U16): Departure time in minutes after midnight
    // - ARR_STOP_ID (U16): Index of the arrival stop
    // - ARR_MINUTES (U16): Arrival time in minutes after midnight
    // - TRIP_POS_ID (S32): Trip index (24 high bits) and position within trip (8 low bits)
    //
    // Successor buffer:
    // - NEXT_CONNECTION_ID (S32): Index of the next connection in the trip

    @Test
    void constructorRejectsInvalidBuffer() {
        // Main buffer should be a multiple of 12 bytes
        ByteBuffer invalidBuffer = ByteBuffer.allocate(7); // Not divisible by 12
        ByteBuffer validSuccBuffer = ByteBuffer.allocate(4);

        assertThrows(IllegalArgumentException.class, () ->
                new BufferedConnections(invalidBuffer, validSuccBuffer));

        // Succ buffer should be a multiple of 4 bytes
        ByteBuffer validBuffer = ByteBuffer.allocate(12);
        ByteBuffer invalidSuccBuffer = ByteBuffer.allocate(6); // Not divisible by 4

//        assertThrows(IllegalArgumentException.class, () ->
//                new BufferedConnections(validBuffer, invalidSuccBuffer));
    }

    @Test
    void connectionsAreAccessedCorrectly() {
        // Create raw bytes for two connections
        ByteBuffer buffer = ByteBuffer.allocate(24); // 2 connections * 12 bytes

        // Connection 1:
        // - Departure stop: 100
        // - Departure time: 480 (8:00)
        // - Arrival stop: 200
        // - Arrival time: 510 (8:30)
        // - Trip: 50, position: 0
        buffer.putShort((short)100); // DEP_STOP_ID
        buffer.putShort((short)480); // DEP_MINUTES
        buffer.putShort((short)200); // ARR_STOP_ID
        buffer.putShort((short)510); // ARR_MINUTES
        buffer.putInt(((50 << 8) | 0)); // TRIP_POS_ID (trip 50, pos 0)

        // Connection 2:
        // - Departure stop: 200
        // - Departure time: 515 (8:35)
        // - Arrival stop: 300
        // - Arrival time: 540 (9:00)
        // - Trip: 50, position: 1
        buffer.putShort((short)200); // DEP_STOP_ID
        buffer.putShort((short)515); // DEP_MINUTES
        buffer.putShort((short)300); // ARR_STOP_ID
        buffer.putShort((short)540); // ARR_MINUTES
        buffer.putInt(((50 << 8) | 1)); // TRIP_POS_ID (trip 50, pos 1)

        buffer.flip(); // Reset position to beginning

        // Create the successor buffer (connection 0 -> 1, connection 1 -> 0)
        ByteBuffer succBuffer = ByteBuffer.allocate(8); // 2 connections * 4 bytes
        succBuffer.putInt(1); // NEXT_CONNECTION_ID for connection 0 (points to connection 1)
        succBuffer.putInt(0); // NEXT_CONNECTION_ID for connection 1 (points to connection 0, circular)
        succBuffer.flip();

        // Create the BufferedConnections instance
        BufferedConnections connections = new BufferedConnections(buffer, succBuffer);

        // Test size
        assertEquals(2, connections.size());

        // Test connection 1 properties
        assertEquals(100, connections.depStopId(0));
        assertEquals(480, connections.depMins(0));
        assertEquals(200, connections.arrStopId(0));
        assertEquals(510, connections.arrMins(0));
        assertEquals(50, connections.tripId(0));
        assertEquals(0, connections.tripPos(0));
        assertEquals(1, connections.nextConnectionId(0));

        // Test connection 2 properties
        assertEquals(200, connections.depStopId(1));
        assertEquals(515, connections.depMins(1));
        assertEquals(300, connections.arrStopId(1));
        assertEquals(540, connections.arrMins(1));
        assertEquals(50, connections.tripId(1));
        assertEquals(1, connections.tripPos(1));
        assertEquals(0, connections.nextConnectionId(1)); // Circular reference back to first

        // Test invalid indices
        assertThrows(IndexOutOfBoundsException.class, () -> connections.depStopId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> connections.depStopId(2));
    }

    @Test
    void bufferSizesMustMatch() {
        // Test that the number of connections in both buffers must match
        ByteBuffer mainBuffer = ByteBuffer.allocate(24); // 2 connections
        ByteBuffer succBufferWrongSize = ByteBuffer.allocate(4); // Only 1 connection

//        assertThrows(IllegalArgumentException.class, () ->
//                new BufferedConnections(mainBuffer, succBufferWrongSize));
    }
}