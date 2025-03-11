package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

public final class BufferedStationAliases implements StationAliases {
    private StructuredBuffer structuredBuffer;
    private List<String> stringTable;
    public final static int ALIAS_ID = 0;
    public final static int STATION_NAME_ID = 1;

    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer){
        Structure structure = new Structure(field(ALIAS_ID, Structure.FieldType.U16),
                field(STATION_NAME_ID, Structure.FieldType.U16));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        this.stringTable = stringTable;
    }
    @Override
    public String alias(int id) {
        return stringTable.get(structuredBuffer.getU16(id,ALIAS_ID));
    }

    @Override
    public String stationName(int id) {
        return stringTable.get(structuredBuffer.getU16(id,STATION_NAME_ID));
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
