package ch.epfl.rechor;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MyBits32_24_8Test2 {

    @Test
    void packWorksOnValidInputs() {
        int bits24 = 0x000F0F; // 24 bits max
        int bits8 = 0x3;       // 8 bits max
        int expected = 0x000F0F03;

        assertEquals(expected, Bits32_24_8.pack(bits24, bits8));
    }

    @Test
    void packThrowsOnBits24TooLarge() {
        int bits24 = 0x1000000; // Trop grand (25 bits)
        int bits8 = 0xFF;       // 8 bits valide

        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(bits24, bits8));
    }

    @Test
    void packThrowsOnBits8TooLarge() {
        int bits24 = 0xFFFFFF; // 24 bits max
        int bits8 = 0x1FF;     // Trop grand (9 bits)

        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(bits24, bits8));
    }

    @Test
    void unpack24ExtractsCorrectValue() {
        int packed = 0x000F0F03;
        int expected = 0x000F0F;

        assertEquals(expected, Bits32_24_8.unpack24(packed));
    }

    @Test
    void unpack8ExtractsCorrectValue() {
        int packed = 0x000F0F03;
        int expected = 0x03;

        assertEquals(expected, Bits32_24_8.unpack8(packed));
    }

    @Test
    void unpack24ExtractsCorrectValueFromMaxInt() {
        int packed = 0xFFFFFF00; // Max 24 bits dans la partie haute
        int expected = 0xFFFFFF;

        assertEquals(expected, Bits32_24_8.unpack24(packed));
    }

    @Test
    void unpack8ExtractsCorrectValueFromMaxInt() {
        int packed = 0xFFFFFFAB; // Partie faible = 0xAB
        int expected = 0xAB;

        assertEquals(expected, Bits32_24_8.unpack8(packed));
    }

}
