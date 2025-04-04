package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Bits32_24_8Test {
    @Test
    void bits32_24_8PackThrowsWhenBits24Or8TooBig() {
        for (var i = 24; i < 32; i += 1) {
            var iFinal = i;
            assertThrows(IllegalArgumentException.class, () -> {
                Bits32_24_8.pack(1 << iFinal, 0);
            });
        }

        for (var i = 8; i < 32; i += 1) {
            var iFinal = i;
            assertThrows(IllegalArgumentException.class, () -> {
                Bits32_24_8.pack(0, 1 << iFinal);
            });
        }
    }

    @Test
    void bits32_24_8Unpack24CorrectlyUnpacks() {
        for (var i = 0; i < (1 << 24); i += 1) {
            var p = Bits32_24_8.pack(i, 0);
            assertEquals(i, Bits32_24_8.unpack24(p));
        }
    }

    @Test
    void bits32_24_8Unpack8CorrectlyUnpacks() {
        for (var i = 0; i < (1 << 8); i += 1) {
            var p = Bits32_24_8.pack(0, i);
            assertEquals(i, Bits32_24_8.unpack8(p));
        }
    }
}