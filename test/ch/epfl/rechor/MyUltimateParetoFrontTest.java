package ch.epfl.rechor;

import ch.epfl.rechor.journey.PackedCriteria;
import ch.epfl.rechor.journey.ParetoFront;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
import static ch.epfl.rechor.journey.PackedCriteria.pack;

public class MyUltimateParetoFrontTest {


    long p1 = 0b0_000000000000_001011010000_0000011L << 32; // (08h00, 3)
    long p2 = 0b0_000000000000_001011010000_0000100L << 32; // (08h00, 4)
    long p3 = 0b0_000000000000_001011010001_0000010L << 32; // (08h01, 2)
    long p4 = 0b0_000000000000_001011010010_0000001L << 32; // (08h02, 1)
    long p5 = 0b0_000000000000_001011010011_0000000L << 32; // (08h03, 0)
    long p6 = 0b0_000000000000_001100101000_0000001L << 32; // (08h04, 1)
    long p20 = 0b0_000000000000_001100110101_0111111L << 32; // (09h41, 63 changements)
    long p21 = 0b0_000000000000_001011010000_0000000L << 32; // (08h00, 0)

    long p7  = 0b0_000000000000_000000000000_0000001L << 32; // (04h00, 1 changement)
    long p8  = 0b0_000000000000_000000000000_0001111L << 32; // (04h00, 15 changements)
    long p9  = 0b0_000000000000_010011111011_0000000L << 32; // (23h59, 0 changement)
    long p10 = 0b0_000000000000_010011111011_0111111L << 32; // (23h59, 63 changements)
    long p11 = 0b0_000000000000_010011111011_1111111L << 32; // (23h59, 127 changements)
    long p12 = 0b0_000000000000_001100101000_0001010L << 32; // (08h04, 10 changements)

    long criteria_1 = 0b0_110100101111_001100101111_0000010L << 32; // (08h00, 09h35, 2)
    long criteria_2 = 0b0_110100101111_001100101111_0001010L << 32; // (08h00, 09h35, 10)
    long criteria_3 = 0b0_110011110011_001101001000_0000000L << 32; // (09h00, 10h00, 0)
    long criteria_4 = 0b0_110011110011_001100001100_0000001L << 32; // (09h00, 09h30, 1)
    long criteria_5 = 0b0_000000000000_001100001100_0000010L << 32; // (-, 09h30, 2)

    // --- Tests de base sur l'ajout de tuples ---
    @Test
    void testAddSingleTupleToEmptyFrontier() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(p21);
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(p21, front.get(8 * 60, 0));
    }

    @Test
    void testAddDominatedTuple() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(p2);
        builder.add(p20); // p20 est dominé par p2
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(p2, front.get(8 * 60, 4));
    }

    @Test
    void testAddDominatingTuple() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(p20);
        builder.add(p2); // p2 domine p20
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(p2, front.get(8 * 60, 4));
    }

    @Test
    void testAddNonDominatingTuple() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(p1);
        builder.add(p3); // p1 et p3 ne se dominent pas mutuellement
        ParetoFront front = builder.build();
        assertEquals(2, front.size());
        assertEquals(p1, front.get(8 * 60, 3));
        assertEquals(p3, front.get((8 * 60 + 1), 2));
    }

    @Test
    void testAddTupleToFullFrontier() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(p5);
        builder.add(p6);
        builder.add(p4);
        ParetoFront front = builder.build();
        assertEquals(2, front.size());
        assertEquals(p4, front.get((8 * 60 + 2), 1));
    }

    // --- Tests sur l'évolution de la frontière avec plusieurs ajouts ---
    @Test
    void testParetoWithNewValues() {
        long[] expectedFront = {p7, p9}; // ordre important
        ParetoFront.Builder pb = new ParetoFront.Builder();
        pb.add(p7);
        pb.add(p8);  // dominé par p7 : non ajouté
        pb.add(p12); // dominé par p7 : non ajouté
        pb.add(p9);  // ajouté car non dominé par p7
        pb.add(p10); // dominé par p9
        pb.add(p11); // dominé par p9
        ParetoFront front = pb.build();
        assertEquals(expectedFront.length, front.size());
        for (int i = 0; i < expectedFront.length; i++) {
            long criteria = front.get(PackedCriteria.arrMins(expectedFront[i]),
                    PackedCriteria.changes(expectedFront[i]));
            assertEquals(expectedFront[i], criteria);
        }
    }

    @Test
    void testAddAllFromEmptyBuilder() {
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b1.addAll(b2);
        ParetoFront front = b1.build();
        assertEquals(0, front.size());
    }

    @Test
    void testAddAllFromNonEmptyBuilder() {
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b2.add(p1);
        b2.add(p3);
        b1.addAll(b2);
        ParetoFront front = b1.build();
        assertEquals(2, front.size());
        assertEquals(p1, front.get(8 * 60, 3));
        assertEquals(p3, front.get((8 * 60 + 1), 2));
    }

    @Test
    void testAddAllWithDominatedAndDominatingTuples() {
        // Test fusion avec tuples dominés
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b1.add(p1);
        b2.add(p2); // dominé par p1
        b1.addAll(b2);
        ParetoFront front1 = b1.build();
        assertEquals(1, front1.size());
        assertEquals(p1, front1.get(8 * 60, 3));

        // Test fusion avec domination inverse
        b1 = new ParetoFront.Builder();
        b2 = new ParetoFront.Builder();
        b1.add(p2);
        b2.add(p1); // p1 domine p2
        b1.addAll(b2);
        ParetoFront front2 = b1.build();
        assertEquals(1, front2.size());
        assertEquals(p1, front2.get(8 * 60, 3));

        // Fusion avec tuples non dominants
        b1 = new ParetoFront.Builder();
        b2 = new ParetoFront.Builder();
        b1.add(p1);
        b2.add(p3);
        b1.addAll(b2);
        ParetoFront front3 = b1.build();
        assertEquals(2, front3.size());
    }

    // --- Tests spécifiques issus de mySuperParetoFrontTest ---
    @Test
    void testGetWithSuperCriteria() {
        long expectedCrit = p4 | 0b10101L;
        ParetoFront front = new ParetoFront.Builder().add(expectedCrit).build();
        assertEquals(expectedCrit, front.get(8 * 60 + 2, 1));
    }

    @Test
    void testSuperParetoFrontSize() {
        ParetoFront front = new ParetoFront.Builder().add(p1).add(p3).add(p4).build();
        assertEquals(3, front.size());
    }

    @Test
    void testTrivialParetoFront() {
        ParetoFront.Builder pb = new ParetoFront.Builder();
        assertEquals(0, pb.build().size());
        assertEquals(ParetoFront.EMPTY.toString(), pb.build().toString());
    }

    @Test
    void testParetoWithOneElementSuper() {
        long[] expected = {criteria_1};
        ParetoFront front = new ParetoFront.Builder().add(criteria_1).build();
        assertEquals(1, front.size());
        long firstElem = front.get(PackedCriteria.arrMins(expected[0]),
                PackedCriteria.changes(expected[0]));
        assertEquals(expected[0], firstElem);
    }

    @Test
    void testParetoWithSuperValues() {
        long[] expected = {p1, p3, p4, p5}; // l'ordre est important
        ParetoFront.Builder pb = new ParetoFront.Builder()
                .add(p1).add(p2).add(p6).add(p3).add(p4).add(p5);
        ParetoFront front = pb.build();
        assertEquals(expected.length, front.size());
        for (int i = 0; i < expected.length; i++) {
            long crit = front.get(PackedCriteria.arrMins(expected[i]),
                    PackedCriteria.changes(expected[i]));
            assertEquals(expected[i], crit);
        }
    }

    // --- Tests sur les méthodes utilisant add(int, int, int) et forEach ---
    @Test
    void testBuilderIsEmptyInitially() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        assertTrue(builder.isEmpty());
    }

    @Test
    void testBuilderAddIncreasesSizeInt() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        assertFalse(builder.isEmpty());
    }

    @Test
    void testBuilderClearResetsSizeInt() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.clear();
        assertTrue(builder.isEmpty());
    }

    @Test
    void testBuilderAddAllCombinesCriteriaInt() {
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        b1.add(10, 2, 1);
        b1.add(15, 1, 1);
        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b2.add(20, 0, 1);
        b1.addAll(b2);
        ParetoFront front = b1.build();
        assertEquals(3, front.size());
    }

    @Test
    void testAddAllHandlesPartiallyDominatedInt() {
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        b1.add(10, 2, 1);
        b1.add(15, 1, 1);
        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b2.add(12, 3, 1); // dominé par (10,2,1)
        b2.add(14, 1, 1); // non dominé
        b1.addAll(b2);
        ParetoFront front = b1.build();
        assertEquals(2, front.size());
    }

    @Test
    void testParetoFrontGetReturnsCorrectCriteriaInt() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        ParetoFront front = builder.build();
        long crit = front.get(10, 2);
        assertEquals(pack(10, 2, 1), crit);
    }

    @Test
    void testParetoFrontGetThrowsExceptionForMissingInt() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        ParetoFront front = builder.build();
        assertThrows(NoSuchElementException.class, () -> front.get(15, 1));
    }

    @Test
    void testBuilderDoesNotAddDominatedCriteriaInt() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(12, 3, 1); // dominé par le premier
        assertEquals(1, builder.build().size());
    }

    @Test
    void testBuilderFullyDominatesInt() {
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        long crit1 = PackedCriteria.withDepMins(pack(10, 2, 1), 5);
        b1.add(crit1);
        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b2.add(pack(15, 3, 1));  // moins optimal
        assertTrue(b1.fullyDominates(b2, 5));
    }

    @Test
    void testBuilderFullyDominatesWithMixedInt() {
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        b1.add(PackedCriteria.withDepMins(pack(10, 2, 1), 5));
        b1.add(PackedCriteria.withDepMins(pack(20, 1, 1), 5));
        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b2.add(pack(15, 3, 1));
        b2.add(pack(5, 1, 1));
        assertFalse(b1.fullyDominates(b2, 5));
    }

    @Test
    void testBuilderFullyDominatesWithEqualInt() {
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        long crit1 = PackedCriteria.withDepMins(pack(10, 2, 1), 5);
        b1.add(crit1);
        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b2.add(PackedCriteria.withDepMins(pack(10, 2, 1), 5));
        assertTrue(b1.fullyDominates(b2, 5));
    }



    // --- Tests sur la gestion des critères liés à l'heure de départ ---
    @Test
    void testBuilderHandlesInvertedDominance() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 3, 1);
        builder.add(12, 2, 1);
        ParetoFront front = builder.build();
        // Les deux critères doivent rester car aucun ne domine l'autre
        assertEquals(2, front.size());
    }

    @Test
    void testBuilderHandlesDifferentDepartureTimes() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(PackedCriteria.withDepMins(pack(10, 2, 1), 5)); // départ à 5h
        builder.add(PackedCriteria.withDepMins(pack(10, 2, 1), 6)); // départ à 6h
        ParetoFront front = builder.build();
        // Si la gestion des heures de départ permet de traiter différemment, un seul tuple devrait être conservé
        assertEquals(1, front.size());
    }

    @Test
    void testBuilderHandlesCriteriaWithoutDepartureTime() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(PackedCriteria.withoutDepMins(pack(10, 2, 1)));
        builder.add(PackedCriteria.withoutDepMins(pack(12, 3, 1))); // dominé par (10,2,1)
        builder.add(PackedCriteria.withoutDepMins(pack(8, 2, 1)));  // domine (10,2,1)
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertTrue(front.get(8, 2) != 0);
    }

    @Test
    void testBuilderHandlesCriteriaWithDifferentPayload() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(10, 2, 1));
        builder.add(pack(10, 2, 2)); // mêmes critères, payload différente
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertTrue(front.get(10, 2) != 0);
    }

    @Test
    void testBuilderHandlesEqualCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(10, 2, 1));
        builder.add(pack(10, 2, 1)); // critère identique
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertTrue(front.get(10, 2) != 0);
    }

    @Test
    void testBuilderHandlesFullCriteriaDominance() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(pack(10, 2, 1));
        builder.add(pack(12, 3, 1)); // dominé par (10,2,1)
        builder.add(pack(8, 2, 1));  // domine (10,2,1)
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertTrue(front.get(8, 2) != 0);
    }

    @Test
    void testGetThrowsExceptionForNonExistentCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(15, 1, 1);
        ParetoFront front = builder.build();
        assertThrows(NoSuchElementException.class, () -> front.get(20, 0));
    }

    @Test
    public void testParetoFrontForEachOnEmptyFront() {
        ParetoFront emptyFront = ParetoFront.EMPTY;
        List<Long> collected = new ArrayList<>();
        emptyFront.forEach(collected::add);
        assertTrue(collected.isEmpty());
    }
}