package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyPackedCriteriaTest3 {

    // Constants for testing
    private static final int VALID_DEP_MINS = 480; // 8:00
    private static final int VALID_ARR_MINS = 540; // 9:00
    private static final int VALID_CHANGES = 2;
    private static final int VALID_PAYLOAD = 0x12345678;

    private static final int MIN_VALID_MINS = -240; // -4:00, minimum valid time
    private static final int MAX_VALID_MINS = 2879; // 47:59, maximum valid time

    @Test
    void packWithoutDepTimeShouldCreateValidPackedCriteria() {
        long packed = PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD);

        assertFalse(PackedCriteria.hasDepMins(packed));
        assertEquals(VALID_ARR_MINS, PackedCriteria.arrMins(packed));
        assertEquals(VALID_CHANGES, PackedCriteria.changes(packed));
        assertEquals(VALID_PAYLOAD, PackedCriteria.payload(packed));
    }

    @Test
    void packWithThenWithoutDepTimeShouldBehaveProperly() {
        long packedWithDep = PackedCriteria.withDepMins(
                PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD),
                VALID_DEP_MINS);

        assertTrue(PackedCriteria.hasDepMins(packedWithDep));
        assertEquals(VALID_DEP_MINS, PackedCriteria.depMins(packedWithDep));

        long packedWithoutDep = PackedCriteria.withoutDepMins(packedWithDep);

        assertFalse(PackedCriteria.hasDepMins(packedWithoutDep));
        assertEquals(VALID_ARR_MINS, PackedCriteria.arrMins(packedWithoutDep));
        assertEquals(VALID_CHANGES, PackedCriteria.changes(packedWithoutDep));
        assertEquals(VALID_PAYLOAD, PackedCriteria.payload(packedWithoutDep));
    }

    @Test
    void withAdditionalChangeShouldIncrementChanges() {
        long packed = PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD);
        long packedWithExtraChange = PackedCriteria.withAdditionalChange(packed);

        assertEquals(VALID_CHANGES + 1, PackedCriteria.changes(packedWithExtraChange));
        // Other values should remain the same
        assertEquals(VALID_ARR_MINS, PackedCriteria.arrMins(packedWithExtraChange));
        assertEquals(VALID_PAYLOAD, PackedCriteria.payload(packedWithExtraChange));
    }

    @Test
    void withPayloadShouldUpdatePayload() {
        long packed = PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD);
        int newPayload = 0xABCDEF12;
        long packedWithNewPayload = PackedCriteria.withPayload(packed, newPayload);

        assertEquals(newPayload, PackedCriteria.payload(packedWithNewPayload));
        // Other values should remain the same
        assertEquals(VALID_ARR_MINS, PackedCriteria.arrMins(packedWithNewPayload));
        assertEquals(VALID_CHANGES, PackedCriteria.changes(packedWithNewPayload));
    }

    @Test
    void dominatesOrIsEqualShouldWorkCorrectly() {
        // Create criteria where the first dominates the second
        long better = PackedCriteria.withDepMins(
                PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES - 1, VALID_PAYLOAD),
                VALID_DEP_MINS + 10);  // Later departure (better), fewer changes (better), same arrival

        long worse = PackedCriteria.withDepMins(
                PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD),
                VALID_DEP_MINS);

        assertTrue(PackedCriteria.dominatesOrIsEqual(better, worse));
        assertFalse(PackedCriteria.dominatesOrIsEqual(worse, better));

        // Equal criteria should dominate each other
        long criteria = PackedCriteria.withDepMins(
                PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD),
                VALID_DEP_MINS);

        long sameCriteria = PackedCriteria.withDepMins(
                PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD + 1), // Different payload shouldn't affect dominance
                VALID_DEP_MINS);

        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria, sameCriteria));
        assertTrue(PackedCriteria.dominatesOrIsEqual(sameCriteria, criteria));

        // Create criteria where neither dominates the other
        long earlierDepartLaterArrive = PackedCriteria.withDepMins(
                PackedCriteria.pack(VALID_ARR_MINS + 30, VALID_CHANGES, VALID_PAYLOAD),
                VALID_DEP_MINS - 30);

        long laterDepartEarlierArrive = PackedCriteria.withDepMins(
                PackedCriteria.pack(VALID_ARR_MINS - 30, VALID_CHANGES, VALID_PAYLOAD),
                VALID_DEP_MINS + 30);

        assertFalse(PackedCriteria.dominatesOrIsEqual(earlierDepartLaterArrive, laterDepartEarlierArrive));
        assertTrue(PackedCriteria.dominatesOrIsEqual(laterDepartEarlierArrive, earlierDepartLaterArrive));
    }

    // Edge cases

    @Test
    void shouldHandleMinimumValidTime() {
        long packed = PackedCriteria.pack(MIN_VALID_MINS, VALID_CHANGES, VALID_PAYLOAD);
        assertEquals(MIN_VALID_MINS, PackedCriteria.arrMins(packed));

        long packedWithDep = PackedCriteria.withDepMins(packed, MIN_VALID_MINS);
        assertEquals(MIN_VALID_MINS, PackedCriteria.depMins(packedWithDep));
    }

    @Test
    void shouldHandleMaximumValidTime() {
        long packed = PackedCriteria.pack(MAX_VALID_MINS, VALID_CHANGES, VALID_PAYLOAD);
        assertEquals(MAX_VALID_MINS, PackedCriteria.arrMins(packed));

        long packedWithDep = PackedCriteria.withDepMins(packed, MAX_VALID_MINS);
        assertEquals(MAX_VALID_MINS, PackedCriteria.depMins(packedWithDep));
    }

    @Test
    void shouldHandleMaximumValidChanges() {
        int maxChanges = (1 << 7) - 1; // Maximum value storable in 7 bits: 127
        long packed = PackedCriteria.pack(VALID_ARR_MINS, maxChanges, VALID_PAYLOAD);
        assertEquals(maxChanges, PackedCriteria.changes(packed));
    }

    @Test
    void shouldHandleNegativePayload() {
        int negativePayload = -1; // 0xFFFFFFFF
        long packed = PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, negativePayload);
        assertEquals(negativePayload, PackedCriteria.payload(packed));
    }

    @Test
    void shouldHandleMaximumValidPayload() {
        int maxPayload = Integer.MAX_VALUE;
        long packed = PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, maxPayload);
        assertEquals(maxPayload, PackedCriteria.payload(packed));
    }

    // Exception tests

    @Test
    void invalidArrTimesShouldThrowException() {
        // Below minimum
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.pack(MIN_VALID_MINS - 1, VALID_CHANGES, VALID_PAYLOAD));

        // Above maximum
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.pack(MAX_VALID_MINS + 1, VALID_CHANGES, VALID_PAYLOAD));
    }

    @Test
    void invalidDepTimesShouldThrowException() {
        long packed = PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD);

        // Below minimum
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.withDepMins(packed, MIN_VALID_MINS - 1));

        // Above maximum
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.withDepMins(packed, MAX_VALID_MINS + 1));
    }

    @Test
    void tooManyChangesShouldThrowException() {
        int tooManyChanges = (1 << 7); // 128, which requires 8 bits

        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.pack(VALID_ARR_MINS, tooManyChanges, VALID_PAYLOAD));
    }

    @Test
    void gettingDepMinsWhenNotPresentShouldThrowException() {
        long packed = PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD);

        assertFalse(PackedCriteria.hasDepMins(packed));
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.depMins(packed));
    }

    @Test
    void dominatesOrIsEqualWithMismatchedCriteriaShouldThrowException() {
        long withDep = PackedCriteria.withDepMins(
                PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD),
                VALID_DEP_MINS);

        long withoutDep = PackedCriteria.pack(VALID_ARR_MINS, VALID_CHANGES, VALID_PAYLOAD);

        // One has departure time, the other doesn't
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.dominatesOrIsEqual(withDep, withoutDep));

        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.dominatesOrIsEqual(withoutDep, withDep));
    }
}