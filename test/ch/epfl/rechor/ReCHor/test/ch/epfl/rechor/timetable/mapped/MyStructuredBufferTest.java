package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.StructuredBuffer;
import ch.epfl.rechor.timetable.mapped.Structure;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.*;

public class MyStructuredBufferTest {

    @Test
    void constructorThrowsOnInvalidBufferSize() {
        Structure struct = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );

        // Buffer size is 3 bytes, but structure size is 2 bytes, so it's not a multiple
        ByteBuffer buffer = ByteBuffer.allocate(3);

        assertThrows(IllegalArgumentException.class, () -> {
            new StructuredBuffer(struct, buffer);
        });
    }

    @Test
    void sizeReturnsCorrectNumberOfElements() {
        Structure struct = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );

        // Buffer size is 6 bytes (3 elements of 2 bytes each)
        ByteBuffer buffer = ByteBuffer.allocate(6);

        StructuredBuffer structBuffer = new StructuredBuffer(struct, buffer);
        assertEquals(3, structBuffer.size());
    }

    @Test
    void getU8ReturnsCorrectValueForValidIndex() {
        Structure struct = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );

        // Create a buffer with 3 elements
        ByteBuffer buffer = ByteBuffer.allocate(3);
        buffer.put((byte) 0x00);
        buffer.put((byte) 0x7F);
        buffer.put((byte) 0xFF);
        buffer.flip();

        StructuredBuffer structBuffer = new StructuredBuffer(struct, buffer);

        assertEquals(0, structBuffer.getU8(0, 0));
        assertEquals(127, structBuffer.getU8(0, 1));
        assertEquals(255, structBuffer.getU8(0, 2)); // Should be 255, not -1 (unsigned)
    }

    @Test
    void getU16ReturnsCorrectValueForValidIndex() {
        Structure struct = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );

        // Create a buffer with 3 elements
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.putShort((short) 0x0000);
        buffer.putShort((short) 0x7FFF);
        buffer.putShort((short) 0xFFFF);
        buffer.flip();

        StructuredBuffer structBuffer = new StructuredBuffer(struct, buffer);

        assertEquals(0, structBuffer.getU16(0, 0));
        assertEquals(32767, structBuffer.getU16(0, 1));
        assertEquals(65535, structBuffer.getU16(0, 2)); // Should be 65535, not -1 (unsigned)
    }

    @Test
    void getS32ReturnsCorrectValueForValidIndex() {
        Structure struct = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );

        // Create a buffer with 3 elements
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putInt(0);
        buffer.putInt(Integer.MAX_VALUE);
        buffer.putInt(Integer.MIN_VALUE);
        buffer.flip();

        StructuredBuffer structBuffer = new StructuredBuffer(struct, buffer);

        assertEquals(0, structBuffer.getS32(0, 0));
        assertEquals(Integer.MAX_VALUE, structBuffer.getS32(0, 1));
        assertEquals(Integer.MIN_VALUE, structBuffer.getS32(0, 2));
    }

    @Test
    void getAllTypesRetrievedCorrectly() {
        Structure struct = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );

        // Create a buffer with 1 element (7 bytes)
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.put((byte) 0xAA);       // U8 field
        buffer.putShort((short) 0xBBCC); // U16 field
        buffer.putInt(0xDDEEFF00);      // S32 field
        buffer.flip();

        StructuredBuffer structBuffer = new StructuredBuffer(struct, buffer);

        assertEquals(170, structBuffer.getU8(0, 0));    // 0xAA = 170
        assertEquals(48076, structBuffer.getU16(1, 0)); // 0xBBCC = 48076
        assertEquals(0xDDEEFF00, structBuffer.getS32(2, 0));
    }

    @Test
    void getThrowsOnInvalidElementIndex() {
        Structure struct = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );

        ByteBuffer buffer = ByteBuffer.allocate(2);
        StructuredBuffer structBuffer = new StructuredBuffer(struct, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            structBuffer.getU8(0, 2); // Only 2 elements, index 2 is invalid
        });
    }

    @Test
    void getThrowsOnInvalidFieldIndex() {
        Structure struct = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );

        ByteBuffer buffer = ByteBuffer.allocate(2);
        StructuredBuffer structBuffer = new StructuredBuffer(struct, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            structBuffer.getU8(1, 0); // Only field 0 exists
        });
    }
}