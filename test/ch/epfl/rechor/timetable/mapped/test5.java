package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class test5 {

    @Test
    void testVehicule(){
        List<String> stringTable = List.of("A", "B", "C", "D", "E", "F", "G");
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 02 00 00 01 03");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BufferedRoutes a = new BufferedRoutes(stringTable, buffer);

        assertEquals(2, a.size());
        assertEquals("C", a.name(0));
        assertEquals(Vehicle.TRAM, a.vehicle(0));
        assertEquals("B", a.name(1));
        assertEquals(Vehicle.BUS, a.vehicle(1));
    }


    @Test
    void tesTrips(){
        List<String> stringTable = List.of("A", "B", "C", "D", "E", "F");
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 02 00 05 00 03 00 01");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BufferedTrips a = new BufferedTrips(stringTable, buffer);
        assertEquals(2, a.size());
        assertEquals("F", a.destination(0));
        assertEquals(2, a.routeId(0));
        assertEquals("B", a.destination(1));
        assertEquals(3, a.routeId(1));
    }

    @Test
    void tesConnections(){

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 02 00 05 00 03 00 01 00 00 00 01");
        byte[] bytes2 = hexFormat.parseHex("00 01 00 02");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ByteBuffer buffer2 = ByteBuffer.wrap(bytes2);
        BufferedConnections a = new BufferedConnections(buffer, buffer2);
        assertEquals(1, a.size());
        assertEquals(1, a.arrMins(0));
        assertEquals(3, a.arrStopId(0));
        assertEquals(2, a.depStopId(0));
        assertEquals(5, a.depMins(0));
        assertEquals(0, a.tripId(0));
        assertEquals(1, a.tripPos(0));
        assertEquals(65538, a.nextConnectionId(0));
    }

    @Test
    void testTransfers(){
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 02 00 05 01 00 03 00 02 00 00 04 00 02 01");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        BufferedTransfers a = new BufferedTransfers(buffer);
        assertEquals(3, a.size());
        assertEquals(2, a.depStationId(0));
        assertEquals(3, a.depStationId(1));
        assertEquals(1, a.minutes(0));
        assertEquals(1, a.arrivingAt(5));
        assertEquals(258, a.arrivingAt(2));
        assertEquals(1, a.minutesBetween(2, 5));
        assertEquals(0, a.minutesBetween(3, 2));

    }

}