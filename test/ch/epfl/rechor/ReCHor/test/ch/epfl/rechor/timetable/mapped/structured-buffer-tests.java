package ch.epfl.rechor.ReCHor.test.ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.mapped.Structure;
import ch.epfl.rechor.timetable.mapped.StructuredBuffer;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MyStructuredBufferTest {

    @Test
    void constructorShouldRejectInvalidBufferSize() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        // Buffer with 3 bytes, not a multiple of the structure size (2 bytes)
        ByteBuffer buffer = ByteBuffer.allocate(3);
        assertThrows(IllegalArgumentException.class, () -> new StructuredBuffer(structure, buffer));
    }

    @Test
    void constructorShouldAcceptValidBufferSize() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        // Buffer with 4 bytes, a multiple of the structure size (2 bytes)
        ByteBuffer buffer = ByteBuffer.allocate(4);
        assertDoesNotThrow(() -> new StructuredBuffer(structure, buffer));
    }

    @Test
    void sizeShouldReturnCorrectNumberOfElements() {
        // Structure with total size of 4 bytes
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.U16)
        );
        
        // Buffer with 12 bytes = 3 elements of 4 bytes each
        ByteBuffer buffer = ByteBuffer.allocate(12);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        assertEquals(3, structuredBuffer.size());
    }

    @Test
    void getU8ShouldReturnCorrectValue() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U8)
        );
        
        // Create a buffer with 2 elements
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put((byte) 42);  // element 0, field 0
        buffer.put((byte) 128); // element 0, field 1
        buffer.put((byte) 255); // element 1, field 0
        buffer.put((byte) 0);   // element 1, field 1
        
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        
        // Note: U8 is interpreted as unsigned, so 128 should be 128, not -128
        assertEquals(42, structuredBuffer.getU8(0, 0));
        assertEquals(128, structuredBuffer.getU8(1, 0));
        assertEquals(255, structuredBuffer.getU8(0, 1));
        assertEquals(0, structuredBuffer.getU8(1, 1));
    }

    @Test
    void getU16ShouldReturnCorrectValue() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        
        // Create a buffer with 2 elements
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        buffer.putShort((short) 0x1234); // element 0
        buffer.putShort((short) 0xFFFF); // element 1
        buffer.rewind();
        
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        
        // Note: U16 is interpreted as unsigned, so 0xFFFF should be 65535
        assertEquals(0x1234, structuredBuffer.getU16(0, 0));
        assertEquals(0xFFFF, structuredBuffer.getU16(0, 1));
    }

    @Test
    void getS32ShouldReturnCorrectValue() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        
        // Create a buffer with 2 elements
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(Integer.MAX_VALUE);    // element 0
        buffer.putInt(Integer.MIN_VALUE);    // element 1
        buffer.rewind();
        
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        
        assertEquals(Integer.MAX_VALUE, structuredBuffer.getS32(0, 0));
        assertEquals(Integer.MIN_VALUE, structuredBuffer.getS32(0, 1));
    }

    @Test
    void getMethodsShouldThrowForInvalidIndices() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );
        
        ByteBuffer buffer = ByteBuffer.allocate(7); // Just 1 element
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);
        
        // Invalid field indices
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU16(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getS32(3, 0));
        
        // Invalid element indices
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU16(1, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getS32(2, 1));
    }
}
