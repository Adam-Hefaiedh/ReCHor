package ch.epfl.rechor.journey;


import static org.junit.jupiter.api.Assertions.*;

import ch.epfl.rechor.journey.PackedCriteria;
import org.junit.jupiter.api.Test;

class MyPackedCriteriaTest2 {

    @Test
    void packAndUnpackWorkCorrectly() {
        int arrMins = 600;  // 10:00
        int changes = 3;
        int payload = 12345678;

        long packed = PackedCriteria.pack(arrMins, changes, payload);

        assertEquals(arrMins, PackedCriteria.arrMins(packed), "arrMins should match original value");
        assertEquals(changes, PackedCriteria.changes(packed), "Changes should match original value");
        assertEquals(payload, PackedCriteria.payload(packed), "Payload should match original value");
    }

    @Test
    void packThrowsExceptionOnInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.pack(-300, 2, 12345));
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.pack(3000, 2, 12345));
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.pack(500, 130, 12345));
    }

    @Test
    void hasDepMinsWorksCorrectly() {
        long criteriaWithoutDep = PackedCriteria.pack(800, 2, 56789);
        long criteriaWithDep = PackedCriteria.withDepMins(criteriaWithoutDep, 400);

        assertFalse(PackedCriteria.hasDepMins(criteriaWithoutDep), "Should not have depMins");
        assertTrue(PackedCriteria.hasDepMins(criteriaWithDep), "Should have depMins");
    }

    @Test
    void depMinsWorksCorrectly() {
        long criteria = PackedCriteria.withDepMins(PackedCriteria.pack(1000, 4, 789), 500);
        assertEquals(500, PackedCriteria.depMins(criteria), "depMins should be 500");
    }

    @Test
    void withoutDepMinsRemovesDepartureTime() {
        long criteria = PackedCriteria.withDepMins(PackedCriteria.pack(1200, 5, 999), 300);
        long withoutDep = PackedCriteria.withoutDepMins(criteria);

        assertFalse(PackedCriteria.hasDepMins(withoutDep), "DepMins should be removed");
    }

    @Test
    void withAdditionalChangeIncrementsChangeCount() {
        long criteria = PackedCriteria.pack(1300, 6, 555);
        long newCriteria = PackedCriteria.withAdditionalChange(criteria);

        assertEquals(7, PackedCriteria.changes(newCriteria), "Change count should increase by 1");
    }

    @Test
    void withPayloadChangesPayloadCorrectly() {
        long criteria = PackedCriteria.pack(1400, 3, 111);
        long newCriteria = PackedCriteria.withPayload(criteria, 99999);

        assertEquals(99999, PackedCriteria.payload(newCriteria), "Payload should be updated");
    }

    @Test
    void dominatesOrIsEqualWorksCorrectly() {
        long criteria1 = PackedCriteria.pack(1200, 2, 100);
        long criteria2 = PackedCriteria.pack(1300, 3, 200);

        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria1, criteria1), "Criteria should be equal");
        assertFalse(PackedCriteria.dominatesOrIsEqual(criteria2, criteria1), "Criteria2 should not dominate Criteria1");
        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria1, criteria2), "Criteria1 should dominate Criteria2");
    }
}