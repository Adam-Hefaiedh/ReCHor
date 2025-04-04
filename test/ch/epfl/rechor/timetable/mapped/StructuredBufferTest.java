package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static org.junit.jupiter.api.Assertions.*;

class StructuredBufferTest {
    @Test
    void structuredBufferConstructorThrowsOnInvalidSize() {
        var structure = new Structure(
                field(0, Structure.FieldType.U16),
                field(1, Structure.FieldType.S32)
        );
        for (var i = 1; i < 6; i += 1) {
            var buffer = ByteBuffer.wrap(new byte[6 + i]);
            assertThrows(IllegalArgumentException.class, () -> {
                new StructuredBuffer(structure, buffer);
            });
        }
    }

    @Test
    void structuredBufferSizeWorks() {
        var structure = new Structure(
                field(0, Structure.FieldType.U16),
                field(1, Structure.FieldType.S32)
        );
        for (var i = 0; i < 10; i += 1) {
            var buffer = ByteBuffer.wrap(new byte[6 * i]);
            var sBuffer = new StructuredBuffer(structure, buffer);
            assertEquals(i, sBuffer.size());
        }
    }

    @Test
    void structuredBufferGetU8Works() {
        var structure = new Structure(
                field(0, Structure.FieldType.U8)
        );
        var buffer = new byte[1 << 8];
        for (var i = 0; i < buffer.length; i += 1) buffer[i] = (byte) i;
        var sBuffer = new StructuredBuffer(structure, ByteBuffer.wrap(buffer));
        for (var i = 0; i < buffer.length; i += 1)
            assertEquals(i, sBuffer.getU8(0, i));
    }

    @Test
    void structuredBufferGetU16Works() throws IOException {
        var structure = new Structure(
                field(0, Structure.FieldType.U16)
        );
        var byteArrayOutputStream = new ByteArrayOutputStream();
        try (var s = new DataOutputStream(byteArrayOutputStream)) {
            for (int i = 0; i < (1 << 16); i += 1) s.writeShort(i);
        }
        var buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        var sBuffer = new StructuredBuffer(structure, buffer);
        for (var i = 0; i < (1 << 16); i += 1)
            assertEquals(i, sBuffer.getU16(0, i));
    }

    @Test
    void structuredBufferGetS32Works() throws IOException {
        var structure = new Structure(
                field(0, Structure.FieldType.S32)
        );

        var rng1 = new Random(2025);
        var byteArrayOutputStream = new ByteArrayOutputStream();
        try (var s = new DataOutputStream(byteArrayOutputStream)) {
            for (int i = 0; i < 10_000; i += 1) s.writeInt(rng1.nextInt());
        }
        var buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        var sBuffer = new StructuredBuffer(structure, buffer);

        var rng2 = new Random(2025);
        for (var i = 0; i < 10_000; i += 1)
            assertEquals(rng2.nextInt(), sBuffer.getS32(0, i));
    }
}