package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static org.junit.jupiter.api.Assertions.*;

class MyParetoFrontTest {

    @Test
    void emptyParetoFrontHasSizeZero() {
        assertEquals(0, ParetoFront.EMPTY.size());
    }

    @Test
    void getThrowsNoSuchElementExceptionForEmptyFront() {
        assertThrows(NoSuchElementException.class, () ->
                ParetoFront.EMPTY.get(480, 0));
    }

    @Test
    void forEachDoesNothingForEmptyFront() {
        List<Long> elements = new ArrayList<>();
        LongConsumer collectElements = elements::add;

        ParetoFront.EMPTY.forEach(collectElements);

        assertTrue(elements.isEmpty());
    }

    @Test
    void builderCreatesEmptyFrontWhenNoElementsAdded() {
        ParetoFront front = new ParetoFront.Builder().build();
        assertEquals(0, front.size());
    }

    @Test
    void addElementToFrontier() {
        int arrMins = 480; // 8:00
        int changes = 2;
        int payload = 42;

        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(arrMins, changes, payload);

        ParetoFront front = builder.build();
        assertEquals(1, front.size());

        long criteria = front.get(arrMins, changes);
        assertEquals(PackedCriteria.pack(arrMins, changes, payload), criteria);
    }

    @Test
    void getThrowsForNonExistentElement() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(480, 2, 42);  // 8:00, 2 changes

        ParetoFront front = builder.build();

        // Different arrTime
        assertThrows(NoSuchElementException.class, () -> front.get(490, 2));

        // Different changes
        assertThrows(NoSuchElementException.class, () -> front.get(480, 3));
    }

    @Test
    void forEachVisitsAllElements() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        // Add several elements
        builder.add(480, 2, 1);  // 8:00, 2 changes
        builder.add(490, 1, 2);  // 8:10, 1 change
        builder.add(500, 0, 3);  // 8:20, 0 changes

        ParetoFront front = builder.build();

        List<Long> visitedElements = new ArrayList<>();
        front.forEach(visitedElements::add);

        assertEquals(3, visitedElements.size());

        // Elements should be in lexicographic order
        assertTrue(visitedElements.contains(PackedCriteria.pack(480, 2, 1)));
        assertTrue(visitedElements.contains(PackedCriteria.pack(490, 1, 2)));
        assertTrue(visitedElements.contains(PackedCriteria.pack(500, 0, 3)));

        // Ensure lexicographic ordering
        assertTrue(visitedElements.get(0) < visitedElements.get(1));
        assertTrue(visitedElements.get(1) < visitedElements.get(2));
    }
}