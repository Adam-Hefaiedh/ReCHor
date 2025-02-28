package ch.epfl.rechor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyBits32_24_8Test {

    @Test
    public void testValidPack() {
        // Pack values within valid range
        assertEquals(0x12345678, Bits32_24_8.pack(0x123456, 0x78));
        assertEquals(0x00000100, Bits32_24_8.pack(0x000001, 0x00));
        assertEquals(0xFFFFFF00, Bits32_24_8.pack(0xFFFFFF, 0x00));
        assertEquals(0x0000000F, Bits32_24_8.pack(0x000000, 0x0F));
    }

    @Test
    public void testPackWithBits24TooLarge() {
        // Value needs more than 24 bits
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(0x1000000, 0x12));
    }

    @Test
    public void testPackWithBits8TooLarge() {
        // Value needs more than 8 bits
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(0x123456, 0x100));
    }

    @Test
    public void testPackWithNegativeBits24() {
        // Negative values are not valid
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(-1, 0x12));
    }

    @Test
    public void testPackWithNegativeBits8() {
        // Negative values are not valid
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(0x123456, -1));
    }

    @Test
    public void testUnpack24() {
        // Extract the higher 24 bits
        assertEquals(0x123456, Bits32_24_8.unpack24(0x12345678));
        assertEquals(0x000000, Bits32_24_8.unpack24(0x000000FF));
        assertEquals(0xFFFFFF, Bits32_24_8.unpack24(0xFFFFFFFF));
        assertEquals(0x000001, Bits32_24_8.unpack24(0x00000123));
    }

    @Test
    public void testUnpack8() {
        // Extract the lower 8 bits
        assertEquals(0x78, Bits32_24_8.unpack8(0x12345678));
        assertEquals(0xFF, Bits32_24_8.unpack8(0x000000FF));
        assertEquals(0xFF, Bits32_24_8.unpack8(0xFFFFFFFF));
        assertEquals(0x23, Bits32_24_8.unpack8(0x00000123));
        assertEquals(0x00, Bits32_24_8.unpack8(0xFFFFFF00));
    }

    @Test
    public void testPackUnpackConsistency() {
        // Ensure packing then unpacking gives original values
        int bits24 = 0x123456;
        int bits8 = 0x78;
        int packed = Bits32_24_8.pack(bits24, bits8);

        assertEquals(bits24, Bits32_24_8.unpack24(packed));
        assertEquals(bits8, Bits32_24_8.unpack8(packed));
    }

}
