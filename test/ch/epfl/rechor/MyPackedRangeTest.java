package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyPackedRangeTest {

    @Test
    public void testValidPack() {
        // Simple range [10, 20)
        assertEquals(0x00000A0A, PackedRange.pack(10, 20));

        // Range starting at 0 [0, 100)
        assertEquals(0x00000064, PackedRange.pack(0, 100));

        // Maximum range length (255) [1000, 1255)
        assertEquals(0x0003E8FF, PackedRange.pack(1000, 1255));

        // Empty range [42, 42)
        assertEquals(0x00002A00, PackedRange.pack(42, 42));

        // Single element range [50, 51)
        assertEquals(0x00003201, PackedRange.pack(50, 51));
    }

    @Test
    public void testInvalidPackTooLargeStart() {
        // Start value requires more than 24 bits (16777216)
        assertThrows(IllegalArgumentException.class,
                () -> PackedRange.pack(0x1000000, 0x1000010));
    }

    @Test
    public void testInvalidPackNegativeStart() {
        // Negative start value
        assertThrows(IllegalArgumentException.class,
                () -> PackedRange.pack(-1, 10));
    }

    @Test
    public void testInvalidPackTooLargeLength() {
        // Length that requires more than 8 bits (256)
        assertThrows(IllegalArgumentException.class,
                () -> PackedRange.pack(100, 356));
    }

    @Test
    public void testInvalidPackEndBeforeStart() {
        // End before start
        assertThrows(IllegalArgumentException.class,
                () -> PackedRange.pack(50, 30));
    }

    @Test
    public void testLength() {
        // Test with different range lengths
        assertEquals(10, PackedRange.length(PackedRange.pack(10, 20)));
        assertEquals(100, PackedRange.length(PackedRange.pack(0, 100)));
        assertEquals(255, PackedRange.length(PackedRange.pack(1000, 1255)));
        assertEquals(0, PackedRange.length(PackedRange.pack(42, 42)));
        assertEquals(1, PackedRange.length(PackedRange.pack(50, 51)));
    }

    @Test
    public void testStartInclusive() {
        // Test extracting start values
        assertEquals(10, PackedRange.startInclusive(PackedRange.pack(10, 20)));
        assertEquals(0, PackedRange.startInclusive(PackedRange.pack(0, 100)));
        assertEquals(1000, PackedRange.startInclusive(PackedRange.pack(1000, 1255)));
        assertEquals(42, PackedRange.startInclusive(PackedRange.pack(42, 42)));
        assertEquals(50, PackedRange.startInclusive(PackedRange.pack(50, 51)));

        // Test with max possible start value (2^24 - 1)
        int maxStart = 0xFFFFFF;
        assertEquals(maxStart, PackedRange.startInclusive(PackedRange.pack(maxStart, maxStart + 1)));
    }

    @Test
    public void testEndExclusive() {
        // Test extracting end values
        assertEquals(20, PackedRange.endExclusive(PackedRange.pack(10, 20)));
        assertEquals(100, PackedRange.endExclusive(PackedRange.pack(0, 100)));
        assertEquals(1255, PackedRange.endExclusive(PackedRange.pack(1000, 1255)));
        assertEquals(42, PackedRange.endExclusive(PackedRange.pack(42, 42)));
        assertEquals(51, PackedRange.endExclusive(PackedRange.pack(50, 51)));
    }

    @Test
    public void testConsistency() {
        // Test that unpacking a packed range gives the original values
        int start = 500;
        int end = 600;
        int packed = PackedRange.pack(start, end);

        assertEquals(start, PackedRange.startInclusive(packed));
        assertEquals(end, PackedRange.endExclusive(packed));
        assertEquals(end - start, PackedRange.length(packed));
    }

    @Test
    public void testBoundaryConditions() {
        // Test with maximum valid values
        int maxStart = 0xFFFFFF; // 16,777,215 (maximum 24-bit value)
        int maxLength = 0xFF;    // 255 (maximum 8-bit value)

        int packed = PackedRange.pack(maxStart, maxStart + maxLength);

        assertEquals(maxStart, PackedRange.startInclusive(packed));
        assertEquals(maxStart + maxLength, PackedRange.endExclusive(packed));
        assertEquals(maxLength, PackedRange.length(packed));
    }
}