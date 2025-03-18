package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Connections;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static ch.epfl.rechor.Bits32_24_8.*;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

public final class BufferedConnections implements Connections {
    private final StructuredBuffer structuredBuffer;
    private final IntBuffer buffer;
    private final static int DEP_STOP_ID = 0;
    private final static int DEP_MINUTES = 1;
    private final static int ARR_STOP_ID = 2;
    private final static int ARR_MINUTES = 3;
    private final static int TRIP_POS_ID = 4;

    public BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer){
        Structure structure = new Structure(
                field(DEP_STOP_ID, Structure.FieldType.U16),
                field(DEP_MINUTES, Structure.FieldType.U16),
                field(ARR_STOP_ID, Structure.FieldType.U16),
                field(ARR_MINUTES, Structure.FieldType.U16),
                field(TRIP_POS_ID, Structure.FieldType.S32));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        this.buffer = succBuffer.asIntBuffer();
    }

    @Override
    public int depStopId(int id) {
        return structuredBuffer.getU16(DEP_STOP_ID,id);
    }

    @Override
    public int depMins(int id) {
        return structuredBuffer.getU16(DEP_MINUTES,id);
    }

    @Override
    public int arrStopId(int id) {
        return structuredBuffer.getU16(ARR_STOP_ID,id);
    }

    @Override
    public int arrMins(int id) {
        return structuredBuffer.getU16(ARR_MINUTES,id);
    }

    @Override
    public int tripId(int id) {
        return unpack24(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    @Override
    public int tripPos(int id) {
        return unpack8(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    @Override
    public int nextConnectionId(int id) {
        return buffer.get(id);
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
