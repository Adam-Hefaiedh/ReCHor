package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.*;

public class MyStructuredBufferTest {

    // Vérifie que le constructeur accepte un buffer valide.
    @Test
    public void testConstructorValidBuffer() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(12);
        assertDoesNotThrow(() -> new StructuredBuffer(structure, buffer));
    }

    // Vérifie que le constructeur rejette un buffer de taille invalide.
    @Test
    public void testConstructorInvalidBuffer() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(10);
        assertThrows(IllegalArgumentException.class, () -> new StructuredBuffer(structure, buffer));
    }

    // Vérifie que size() retourne la taille correcte avec un buffer non vide.
    @Test
    public void testSize() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(6);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertEquals(1, sb.size());
    }

    // Vérifie que size() retourne zéro avec un buffer vide.
    @Test
    public void testSizeWithEmptyBuffer() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(0);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertEquals(0, sb.size());
    }

    // Vérifie que size() retourne le bon nombre pour un buffer exactement multiple de la structure.
    @Test
    public void testSizeWithExactMultipleBuffer() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16),
                Structure.field(1, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(6);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertEquals(1, sb.size());
    }

    // Vérifie que getU8() retourne 255 pour un champ U8.
    @Test
    public void testGetU8() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(0, (byte) 255);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertEquals(255, sb.getU8(0, 0));
    }

    // Vérifie que getU16() retourne 65535 pour un champ U16.
    @Test
    public void testGetU16() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(0, (short) 65535);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertEquals(65535, sb.getU16(0, 0));
    }

    // Vérifie que getS32() retourne -123456 pour un champ S32.
    @Test
    public void testGetS32() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(0, -123456);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertEquals(-123456, sb.getS32(0, 0));
    }

    // Vérifie que getU8() lève une exception si l'indice du champ est invalide.
    @Test
    public void testGetU8InvalidFieldIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        ByteBuffer buffer = ByteBuffer.allocate(1);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> sb.getU8(1, 0));
    }

    // Vérifie que getU16() lève une exception si l'indice du champ est invalide.
    @Test
    public void testGetU16InvalidFieldIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        ByteBuffer buffer = ByteBuffer.allocate(2);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> sb.getU16(1, 0));
    }

    // Vérifie que getS32() lève une exception si l'indice du champ est invalide.
    @Test
    public void testGetS32InvalidFieldIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(4);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> sb.getS32(1, 0));
    }

    // Vérifie que getU8() lève une exception si l'indice de l'élément est invalide.
    @Test
    public void testGetU8InvalidElementIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        ByteBuffer buffer = ByteBuffer.allocate(1);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> sb.getU8(0, 1));
    }

    // Vérifie que getU16() lève une exception si l'indice de l'élément est invalide.
    @Test
    public void testGetU16InvalidElementIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        ByteBuffer buffer = ByteBuffer.allocate(2);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> sb.getU16(0, 1));
    }

    // Vérifie que getS32() lève une exception si l'indice de l'élément est invalide.
    @Test
    public void testGetS32InvalidElementIndex() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(4);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertThrows(IndexOutOfBoundsException.class, () -> sb.getS32(0, 1));
    }

    // Vérifie que getU8() lit correctement plusieurs éléments.
    @Test
    public void testGetU8MultipleElements() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put(0, (byte) 255);
        buffer.put(1, (byte) 128);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertEquals(255, sb.getU8(0, 0));
        assertEquals(128, sb.getU8(0, 1));
    }

    // Vérifie que getU16() lit correctement plusieurs éléments.
    @Test
    public void testGetU16MultipleElements() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U16)
        );
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putShort(0, (short) 65535);
        buffer.putShort(2, (short) 32768);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertEquals(65535, sb.getU16(0, 0));
        assertEquals(32768, sb.getU16(0, 1));
    }

    // Vérifie que getS32() lit correctement plusieurs éléments.
    @Test
    public void testGetS32MultipleElements() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(0, -123456);
        buffer.putInt(4, 123456);
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        assertEquals(-123456, sb.getS32(0, 0));
        assertEquals(123456, sb.getS32(0, 1));
    }

    // Vérifie que la lecture de plusieurs champs sur plusieurs éléments fonctionne correctement.
    @Test
    public void testMultipleElementsAndFields() {
        Structure structure = new Structure(
                Structure.field(0, Structure.FieldType.U8),
                Structure.field(1, Structure.FieldType.U16),
                Structure.field(2, Structure.FieldType.S32)
        );
        ByteBuffer buffer = ByteBuffer.allocate(14);
        buffer.put(0, (byte) 255);                // Élément 0, Champ 0 (U8)
        buffer.putShort(1, (short) 65535);         // Élément 0, Champ 1 (U16)
        buffer.putInt(3, -123456);                // Élément 0, Champ 2 (S32)
        buffer.put(7, (byte) 128);                // Élément 1, Champ 0 (U8)
        buffer.putShort(8, (short) 32768);         // Élément 1, Champ 1 (U16)
        buffer.putInt(10, 123456);                // Élément 1, Champ 2 (S32)
        StructuredBuffer sb = new StructuredBuffer(structure, buffer);
        // Élément 0
        assertEquals(255, sb.getU8(0, 0));
        assertEquals(65535, sb.getU16(1, 0));
        assertEquals(-123456, sb.getS32(2, 0));
        // Élément 1
        assertEquals(128, sb.getU8(0, 1));
        assertEquals(32768, sb.getU16(1, 1));
        assertEquals(123456, sb.getS32(2, 1));
    }
}