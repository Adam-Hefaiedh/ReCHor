package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyParetoFrontTest2 {

    /**
     * Tests the scenario described in the documentation with six possibilities:
     * 1. Arrive at
     * 2. Arrive at
     * 3. Arrive at
     * 4. Arrive at
     * 5. Arrive at
     * 6. Arrive at
     *
     * The resulting Pareto front should contain points 1, 3, 4, and 5.
     */
    @Test
    void documentationExample() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        // Convert times to minutes after midnight
        int time_8_00 = 8 * 60;      // 8:00
        int time_8_01 = 8 * 60 + 1;  // 8:01
        int time_8_02 = 8 * 60 + 2;  // 8:02
        int time_8_03 = 8 * 60 + 3;  // 8:03
        int time_8_04 = 8 * 60 + 4;  // 8:04

        // Add the six points from the example in the specified order: 1, 2, 6, 3, 4, 5
        // Point 1: (8:00, 3 changes)
        builder.add(time_8_00, 3, 1);
        assertEquals(1, builder.build().size());

        // Point 2: (8:00, 4 changes) - dominated by Point 1
        builder.add(time_8_00, 4, 2);
        assertEquals(1, builder.build().size());

        // Point 6: (8:04, 1 change)
        builder.add(time_8_04, 1, 6);
        assertEquals(2, builder.build().size());

        // Point 3: (8:01, 2 changes)
        builder.add(time_8_01, 2, 3);
        assertEquals(3, builder.build().size());

        // Point 4: (8:02, 1 change) - dominates Point 6
        builder.add(time_8_02, 1, 4);

        // After adding Point 4, Point 6 should be eliminated
        ParetoFront frontAfterPoint4 = builder.build();
        assertEquals(3, frontAfterPoint4.size());

        // Check that Point 6 is not in the frontier
        assertThrows(NoSuchElementException.class, () -> frontAfterPoint4.get(time_8_04, 1));

        // Point 5: (8:03, 0 changes)
        builder.add(time_8_03, 0, 5);

        // Final frontier should have 4 points: 1, 3, 4, 5
        ParetoFront finalFront = builder.build();
        assertEquals(4, finalFront.size());

        // Check that all expected points are in the frontier
        assertEquals(PackedCriteria.pack(time_8_00, 3, 1), finalFront.get(time_8_00, 3));
        assertEquals(PackedCriteria.pack(time_8_01, 2, 3), finalFront.get(time_8_01, 2));
        assertEquals(PackedCriteria.pack(time_8_02, 1, 4), finalFront.get(time_8_02, 1));
        assertEquals(PackedCriteria.pack(time_8_03, 0, 5), finalFront.get(time_8_03, 0));

        // Check the order when iterating
        List<Long> elements = new ArrayList<>();
        finalFront.forEach(elements::add);

        assertEquals(4, elements.size());

        // Elements should be in lexicographic order
        assertEquals(PackedCriteria.pack(time_8_00, 3, 1), elements.get(0));
        assertEquals(PackedCriteria.pack(time_8_01, 2, 3), elements.get(1));
        assertEquals(PackedCriteria.pack(time_8_02, 1, 4), elements.get(2));
        assertEquals(PackedCriteria.pack(time_8_03, 0, 5), elements.get(3));
    }

    @Test
    void testCasesWithDepartureTime() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        // Add criteria with departure times
        long criteria1 = PackedCriteria.withDepMins(
                PackedCriteria.pack(480, 2, 1), 420);  // Dep 7:00, Arr 8:00, 2 changes

        long criteria2 = PackedCriteria.withDepMins(
                PackedCriteria.pack(480, 3, 2), 430);  // Dep 7:10, Arr 8:00, 3 changes
        long criteria3 = PackedCriteria.withDepMins(
                PackedCriteria.pack(490, 1, 3), 440);  // Dep 7:20, Arr 8:10, 1 change

        builder.add(criteria1);
        builder.add(criteria2);
        builder.add(criteria3);

        ParetoFront front = builder.build();

        // criteria2 should be dominated by criteria1
        assertEquals(3, front.size());

        // Verify we have the correct criteria
        List<Long> elements = new ArrayList<>();
        front.forEach(elements::add);

        assertTrue(elements.contains(criteria1));
        assertTrue(elements.contains(criteria3));
        assertTrue(elements.contains(criteria2));
    }

    @Test
    void addingAlreadyDominatedElementDoesntChangeBuilder() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        // Add element (8:00, 1 change)
        builder.add(480, 1, 1);

        // Try to add dominated element (8:10, 2 changes) - worse on both criteria
        builder.add(490, 2, 2);

        // Builder should still have only one element
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(PackedCriteria.pack(480, 1, 1), front.get(480, 1));
    }

    @Test
    void nonDominatedElementsFormCorrectParetoFront() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        // These should all be part of the Pareto front (none dominates another)
        builder.add(480, 3, 1);  // 8:00, 3 changes
        builder.add(490, 2, 2);  // 8:10, 2 changes
        builder.add(500, 1, 3);  // 8:20, 1 change
        builder.add(510, 0, 4);  // 8:30, 0 changes

        ParetoFront front = builder.build();
        assertEquals(4, front.size());

        // All elements should be in the front
        assertEquals(PackedCriteria.pack(480, 3, 1), front.get(480, 3));
        assertEquals(PackedCriteria.pack(490, 2, 2), front.get(490, 2));
        assertEquals(PackedCriteria.pack(500, 1, 3), front.get(500, 1));
        assertEquals(PackedCriteria.pack(510, 0, 4), front.get(510, 0));
    }
}