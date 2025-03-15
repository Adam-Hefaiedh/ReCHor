package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.*;

class MyStructuredBufferTest2 {

    @Test
    void constructorThrowsWithInvalidBufferSize() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.U16)
        );
        // Structure size is 4 bytes, but buffer size is 5 bytes (not a multiple)
        ByteBuffer buffer = ByteBuffer.allocate(5);

        assertThrows(IllegalArgumentException.class,
                () -> new StructuredBuffer(structure, buffer));
    }

    @Test
    void constructorAcceptsValidBufferSize() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.U16)
        );
        // Structure size is 4 bytes, and buffer size is 8 bytes (a valid multiple)
        ByteBuffer buffer = ByteBuffer.allocate(8);

        assertDoesNotThrow(() -> new StructuredBuffer(structure, buffer));
    }

    @Test
    void sizeReturnsCorrectNumberOfElements() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.U16)
        );
        // Structure size is 4 bytes, buffer has 12 bytes -> 3 elements
        ByteBuffer buffer = ByteBuffer.allocate(12);
        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);

        assertEquals(3, structuredBuffer.size());
    }

    @Test
    void getU8ReturnsCorrectValue() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );

        // Create buffer with two elements: [42, 255]
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put((byte) 42);
        buffer.put((byte) -1); // -1 in signed byte is 255 in unsigned
        buffer.flip(); // Reset position to begin reading

        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);

        assertEquals(42, structuredBuffer.getU8(0, 0));
        assertEquals(255, structuredBuffer.getU8(0, 1)); // Should interpret as unsigned
    }

    @Test
    void getU16ReturnsCorrectValue() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );

        // Create buffer with two elements
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort((short) 12345);
        buffer.putShort((short) -1); // -1 in signed short is 65535 in unsigned
        buffer.flip();

        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);

        assertEquals(12345, structuredBuffer.getU16(0, 0));
        assertEquals(65535, structuredBuffer.getU16(0, 1)); // Should interpret as unsigned
    }

    @Test
    void getS32ReturnsCorrectValue() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );

        // Create buffer with two elements
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(123456789);
        buffer.putInt(-987654321);
        buffer.flip();

        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);

        assertEquals(123456789, structuredBuffer.getS32(0, 0));
        assertEquals(-987654321, structuredBuffer.getS32(0, 1)); // Should remain signed
    }

    @Test
    void multipleFieldsAreAccessedCorrectly() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );

        // 1 U8 + 1 U16 + 1 S32 = 7 bytes per element
        ByteBuffer buffer = ByteBuffer.allocate(14); // 2 elements

        // First element
        buffer.put((byte) 10);       // U8 field
        buffer.putShort((short) 20); // U16 field
        buffer.putInt(30);           // S32 field

        // Second element
        buffer.put((byte) 40);        // U8 field
        buffer.putShort((short) 50);  // U16 field
        buffer.putInt(-60);           // S32 field

        buffer.flip();

        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);

        // First element
        assertEquals(10, structuredBuffer.getU8(0, 0));
        assertEquals(20, structuredBuffer.getU16(1, 0));
        assertEquals(30, structuredBuffer.getS32(2, 0));

        // Second element
        assertEquals(40, structuredBuffer.getU8(0, 1));
        assertEquals(50, structuredBuffer.getU16(1, 1));
        assertEquals(-60, structuredBuffer.getS32(2, 1));
    }

    @Test
    void getMethodsThrowWithInvalidIndices() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );

        ByteBuffer buffer = ByteBuffer.allocate(7); // 1 element
        for (int i = 0; i < 7; i++) {
            buffer.put((byte) i);
        }
        buffer.flip();

        StructuredBuffer structuredBuffer = new StructuredBuffer(structure, buffer);

        // Invalid field indices
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(3, 0));

        // Invalid element indices
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(0, 1));

        // Correct field type must be used
//        assertThrows(IllegalArgumentException.class, () -> structuredBuffer.getU16(0, 0)); // Field 0 is U8, not U16
//        assertThrows(IllegalArgumentException.class, () -> structuredBuffer.getU8(1, 0));  // Field 1 is U16, not U8
//        assertThrows(IllegalArgumentException.class, () -> structuredBuffer.getS32(1, 0)); // Field 1 is U16, not S32
    }
}