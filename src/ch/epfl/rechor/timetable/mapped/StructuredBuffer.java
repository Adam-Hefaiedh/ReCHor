package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Preconditions;
import ch.epfl.rechor.timetable.Indexed;

import java.nio.ByteBuffer;

public class StructuredBuffer {

    private Structure structure;
    private ByteBuffer buffer;
    private int size;

    public StructuredBuffer(Structure structure, ByteBuffer buffer){
       this.structure = structure;
       this.buffer = buffer;
        Preconditions.checkArgument(buffer.capacity() % structure.TotalSize() == 0);
        size = buffer.capacity()/ structure.TotalSize();
    }

    public int size(){
        return size;
    }

    public int getU8(int fieldIndex, int elementIndex){
        int offset = structure.offset(fieldIndex, elementIndex);
        byte value = buffer.get(offset);
        return Byte.toUnsignedInt(value);
    }

    public int getU16(int fieldIndex, int elementIndex){
        int offset = structure.offset(fieldIndex, elementIndex);
        short value = buffer.get(offset);
        return Short.toUnsignedInt(value);
    }

    public int getS32(int fieldIndex, int elementIndex){
        int offset = structure.offset(fieldIndex, elementIndex);
        return buffer.getInt(offset);
    }

}
