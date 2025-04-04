package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PackedRangeTest {
    @Test
    void packedRangePackThrowsWithIllegalStartOrEnd() {
        for (var startInclusive : new int[]{-1, (1 << 24)}) {
            assertThrows(IllegalArgumentException.class, () -> {
                PackedRange.pack(startInclusive, startInclusive + 1);
            });
        }

        for (var length : new int[]{-1, (1 << 8)}) {
            var startInclusive = 1000;
            assertThrows(IllegalArgumentException.class, () -> {
                PackedRange.pack(startInclusive, startInclusive + length);
            });
        }
    }

    @Test
    void packedRangeStartInclusiveWorks() {
        var length = 1;
        for (var startInclusive = 0; startInclusive < (1 << 24); startInclusive += 1) {
            var p = PackedRange.pack(startInclusive, startInclusive + length);
            assertEquals(startInclusive, PackedRange.startInclusive(p));
        }
    }

    @Test
    void packedRangeLengthWorks() {
        var startInclusive = 1000;
        for (var length = 0; length < (1 << 8); length += 1) {
            var p = PackedRange.pack(startInclusive, startInclusive + length);
            assertEquals(length, PackedRange.length(p));
        }
    }

    @Test
    void packedRangeEndExclusiveWorks() {
        var startInclusive = (1 << 24) - 1;
        for (var length = 0; length < (1 << 8); length += 1) {
            var endExclusive = startInclusive + length;
            var p = PackedRange.pack(startInclusive, endExclusive);
            assertEquals(endExclusive, PackedRange.endExclusive(p));
        }
    }
}