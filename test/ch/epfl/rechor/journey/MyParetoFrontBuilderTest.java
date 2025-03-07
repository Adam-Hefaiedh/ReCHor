package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyParetoFrontBuilderTest {

    @Test
    void newBuilderIsEmpty() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        assertTrue(builder.isEmpty());
    }

    @Test
    void builderNotEmptyAfterAddingElement() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(480, 2, 42);
        assertFalse(builder.isEmpty());
    }

    @Test
    void clearMakesBuilderEmpty() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(480, 2, 42);
        assertFalse(builder.isEmpty());

        builder.clear();
        assertTrue(builder.isEmpty());
    }

    @Test
    void copyConstructorCreatesSeparateBuilder() {
        ParetoFront.Builder original = new ParetoFront.Builder();
        original.add(480, 2, 42);

        ParetoFront.Builder copy = new ParetoFront.Builder(original);
        assertFalse(copy.isEmpty());

        // Modifying original doesn't affect copy
        original.clear();
        assertTrue(original.isEmpty());
        assertFalse(copy.isEmpty());
    }

    @Test
    void addDoesNotInsertDominatedElement() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        // Add an element (480 min, 2 changes)
        builder.add(480, 2, 1);

        // Add a dominated element (500 min, 3 changes) - worse on both criteria
        builder.add(500, 3, 2);

        List<Long> elements = new ArrayList<>();
        builder.forEach(elements::add);
        // Should only contain the first element
        assertEquals(1, elements.size());
        assertEquals(PackedCriteria.pack(480, 2, 1), elements.get(0));
    }

    @Test
    void addRemovesDominatedElements() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        // Add 3 elements to start
        builder.add(500, 3, 1);  // 8:20, 3 changes
        builder.add(490, 2, 2);  // 8:10, 2 changes
        builder.add(510, 1, 3);  // 8:30, 1 change
        // Add element that dominates the first one (better on both criteria)
        builder.add(480, 2, 4);  // 8:00, 2 changes - dominates (490, 2)

        List<Long> elements = new ArrayList<>();
        builder.forEach(elements::add);

        // Should contain these three elements (in lexicographic order)
        assertEquals(2, elements.size());
        assertEquals(PackedCriteria.pack(480, 2, 4), elements.get(0));
        assertEquals(PackedCriteria.pack(510, 1, 3), elements.get(1));
    }

    @Test
    void addAllCombinesTwoBuilders() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(480, 2, 1);
        builder1.add(500, 0, 2);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(490, 1, 3);
        builder2.add(510, 0, 4);

        builder1.addAll(builder2);

        List<Long> elements = new ArrayList<>();
        builder1.forEach(elements::add);

        assertEquals(3, elements.size());
        assertTrue(elements.contains(PackedCriteria.pack(480, 2, 1)));
        assertTrue(elements.contains(PackedCriteria.pack(490, 1, 3)));
        assertTrue(elements.contains(PackedCriteria.pack(500, 0, 2)));
    }

    @Test
    void addAllRemovesDominatedElements() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(480, 1, 1);  // Better than (500, 2) in builder2

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(500, 2, 2);  // Dominated by (480, 1) in builder1
        builder2.add(490, 0, 3);  // Not dominated

        builder1.addAll(builder2);

        List<Long> elements = new ArrayList<>();
        builder1.forEach(elements::add);

        assertEquals(2, elements.size());
        assertTrue(elements.contains(PackedCriteria.pack(480, 1, 1)));
        assertTrue(elements.contains(PackedCriteria.pack(490, 0, 3)));
    }

    @Test
    void fullyDominatesWithEmptyBuilders() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        // Empty builder dominates empty builder
        assertTrue(builder1.fullyDominates(builder2, 480));

        builder2.add(500, 2, 1);

        // Empty builder doesn't dominate non-empty builder
        assertFalse(builder1.fullyDominates(builder2, 480));

        // Non-empty builder dominates empty builder
        assertTrue(builder2.fullyDominates(builder1, 480));
    }

    @Test
    void fullyDominatesWhenAllElementsDominated() {
        ParetoFront.Builder dominant = new ParetoFront.Builder();
        dominant.add(PackedCriteria.withDepMins(PackedCriteria.pack(480, 1, 1), 480));  // 8:00, 1 change

        ParetoFront.Builder dominated = new ParetoFront.Builder();
        dominated.add(PackedCriteria.withDepMins(PackedCriteria.pack(500, 2, 2), 480));  // 8:20, 2 changes

        // dominant fully dominates dominated
        assertTrue(dominant.fullyDominates(dominated, 480));

        // dominated doesn't fully dominate dominant
        assertFalse(dominated.fullyDominates(dominant, 480));
    }

    @Test
    void fullyDominatesWhenNotAllElementsDominated() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(PackedCriteria.withDepMins(PackedCriteria.pack(480, 1, 1), 480));  // 8:00, 1 change

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(PackedCriteria.withDepMins(PackedCriteria.pack(500, 2, 2), 480));  // 8:20, 2 changes - dominated by builder1
        builder2.add(PackedCriteria.withDepMins(PackedCriteria.pack(470, 0, 3), 480));  // 7:50, 0 changes - not dominated by builder1

        // builder1 doesn't fully dominate builder2
        assertFalse(builder1.fullyDominates(builder2, 480));
    }

    @Test
    void chainMethodCallsWork() {
        ParetoFront.Builder builder = new ParetoFront.Builder()
                .add(480, 2, 1)
                .add(490, 1, 2)
                .add(500, 0, 3);

        ParetoFront front = builder.build();
        assertEquals(3, front.size());
    }

    @Test
    void addPackedTupleWorks() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        long packedTuple = PackedCriteria.pack(480, 2, 42);
        builder.add(packedTuple);

        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(packedTuple, front.get(480, 2));
    }

    @Test
    void forEachIteratesInLexicographicOrder() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        // Add elements in non-lexicographic order
        builder.add(500, 0, 3);
        builder.add(480, 2, 1);
        builder.add(490, 1, 2);

        List<Long> elements = new ArrayList<>();
        builder.forEach(elements::add);

        // Elements should be in lexicographic order
        assertEquals(3, elements.size());
        assertEquals(PackedCriteria.pack(480, 2, 1), elements.get(0));
        assertEquals(PackedCriteria.pack(490, 1, 2), elements.get(1));
        assertEquals(PackedCriteria.pack(500, 0, 3), elements.get(2));
    }
}