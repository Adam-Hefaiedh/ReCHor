package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

public final class BufferedPlatforms implements Platforms {
    private StructuredBuffer structuredBuffer;
    private List<String> stringTable;
    public final static int NAME_ID = 0;
    public final static int STATION_ID = 1;

    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer){
        Structure structure = new Structure(field(NAME_ID, Structure.FieldType.U16),
                field(STATION_ID, Structure.FieldType.U16));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        this.stringTable = stringTable;
    }

    @Override
    public String name(int id) {
        return stringTable.get(structuredBuffer.getU16(id,NAME_ID));
    }

    @Override
    public int stationId(int id) {
        return Integer.getInteger(stringTable.get(structuredBuffer.getS32(id, STATION_ID)));
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
