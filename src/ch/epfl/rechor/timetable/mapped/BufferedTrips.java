package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Trips;

import java.nio.ByteBuffer;
import java.util.List;

public final class BufferedTrips implements Trips {

    private final StructuredBuffer structuredBuffer;
    private List<String> stringTable;
    private int ROUTE_ID_Index = 0;
    private int DESTINATION_ID_Index = 1;


    public BufferedTrips(List<String> stringTable, ByteBuffer buffer){
    Structure structure = new Structure(new Structure.Field(ROUTE_ID_Index, Structure.FieldType.U16),
            new Structure.Field(DESTINATION_ID_Index, Structure.FieldType.U16));
    structuredBuffer = new StructuredBuffer(structure, buffer);
    this.stringTable = stringTable;

    }
    @Override
    public int routeId(int id) {
        return structuredBuffer.getU16(ROUTE_ID_Index,id);
    }

    @Override
    public String destination(int id) {
        return stringTable.get(structuredBuffer.getU16(DESTINATION_ID_Index,id));
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
