package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static java.lang.Math.scalb;

public final class BufferedStations implements Stations {

    private StructuredBuffer structuredBuffer;
    private List<String> stringTable;
    public final static int NAME_ID = 0;
    public final static int LON = 1;
    public final static int LAT = 2;

    public BufferedStations(List<String> stringTable, ByteBuffer buffer){
        Structure structure = new Structure(field(NAME_ID, Structure.FieldType.U16),
                field(LON, Structure.FieldType.S32),
                field(LAT, Structure.FieldType.S32));
       structuredBuffer = new StructuredBuffer(structure, buffer);
       this.stringTable = stringTable;
    }

    @Override
    public String name(int id) {
        return stringTable.get(structuredBuffer.getU16(id, NAME_ID));
    }

    @Override
    public double longitude(int id) {
        int longitude = Integer.getInteger(stringTable.get(structuredBuffer.getS32(id, LON)));
        return scalb(longitude*360,-32);
    }

    @Override
    public double latitude(int id) {
        int latitude = Integer.getInteger(stringTable.get(structuredBuffer.getS32(id, LAT)));
        return scalb(latitude*360,-32);
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
