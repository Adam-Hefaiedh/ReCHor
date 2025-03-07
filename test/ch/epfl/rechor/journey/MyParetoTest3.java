package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


public class MyParetoTest3 {

    @Test
    void builderAddToEmptyFront() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long tuple1 = PackedCriteria.pack(480, 3, 0);
        long tuple2 = PackedCriteria.pack(480, 4, 0);// 8h00, 2 changements
        long tuple3 = PackedCriteria.pack(484, 1, 0);// 8h00, 2 changements
        long tuple4 = PackedCriteria.pack(481, 2, 0);// 8h00, 2 changements
        long tuple5 = PackedCriteria.pack(482, 1, 0);// 8h00, 2 changements
        long tuple6 = PackedCriteria.pack(483, 0, 0);// 8h00, 2 changements

        System.out.println(builder);
        builder.add(tuple1);
        System.out.println(builder);
        builder.add(tuple2);
        System.out.println(builder);
        builder.add(tuple3);
        System.out.println(builder);
        builder.add(tuple4);
        System.out.println(builder);
        builder.add(tuple5);
        System.out.println(builder);
        builder.add(tuple6);
        System.out.println(builder);

        ParetoFront front = builder.build();
        assertEquals(4, front.size());
        assertEquals(tuple1, front.get(480, 3));
        assertEquals(tuple4, front.get(481, 2));
        assertEquals(tuple5, front.get(482, 1));
        assertEquals(tuple6, front.get(483, 0));

    }

    @Test
    void builderAddDominatedTupleDoesNothing() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long tuple1 = PackedCriteria.pack(480, 5, 0);
        long tuple2 = PackedCriteria.pack(400, 20, 0);
        long tuple3 = PackedCriteria.pack(480, 10, 0);

        builder.add(tuple1);
        builder.add(tuple2);
        builder.add(tuple3);

        ParetoFront pareto = builder.build();
        assertEquals(2, pareto.size());
        assertTrue(pareto.get(480, 5) == tuple1);
    }

    @Test
    void builderAddDominatingTupleRemovesDominated() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long tuple1 = PackedCriteria.pack(500, 3, 0); // Dominé
        long tuple2 = PackedCriteria.pack(480, 2, 0); // Domine

        builder.add(tuple1);
        builder.add(tuple2);

        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(tuple2, front.get(480, 2));
    }

    @Test
    void builderAddNonDominatedTuplesRetainsBoth() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long tuple1 = PackedCriteria.pack(480, 2, 0); // Arrivée tôt, plus de changements
        long tuple2 = PackedCriteria.pack(490, 1, 0); // Arrivée plus tard, moins de changements

        builder.add(tuple1);
        builder.add(tuple2);

        assertEquals(2, builder.build().size());
    }

    @Test
    void fullyDominatesReturnsTrueWhenAllDominated() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();

        long tuple1 = PackedCriteria.pack(480, 2, 0);
        long tuple2 = PackedCriteria.pack(490, 1, 0);
        tuple1 = PackedCriteria.withDepMins(tuple1, 100);
        tuple2 = PackedCriteria.withDepMins(tuple2, 100);

        builder1.add(tuple1); // Domine tout
        builder1.add(tuple2);

        System.out.println(builder1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(PackedCriteria.pack(500, 2, 0));
        builder2.add(PackedCriteria.pack(510, 3, 0));

        System.out.println(builder2);

        assertTrue(builder1.fullyDominates(builder2, 100));
    }
    @Test
    void builderResizesInternalArrayWhenFull() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        for (int i = 0; i < 126; i++) {
            builder.add(PackedCriteria.pack(480 + i, 127-i, 0));
        }

        assertTrue(builder.build().size() > 0);
        assertTrue(builder.build().size() == 126);

    }

    @Test
    void getThrowsForNonExistentCriteria() {
        ParetoFront front = ParetoFront.EMPTY;
        assertThrows(NoSuchElementException.class, () -> front.get(0, 0));
    }

    @Test
    void builderAddsEdgeCaseValues() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        long minTuple = PackedCriteria.pack(-240, 127, 0);
        long maxTuple = PackedCriteria.pack(2879, 0, 0);

        builder.add(minTuple);
        builder.add(maxTuple);

        assertEquals(2, builder.build().size());
    }

    @Test
    void builderMaintainsLexOrderAfterAdditions() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(PackedCriteria.pack(500, 2, 0));
        builder.add(PackedCriteria.pack(480, 3, 0));
        builder.add(PackedCriteria.pack(490, 1, 0));

        List<Long> tuples = new ArrayList<>();
        builder.forEach(tuples::add);



        assertTrue(tuples.get(0) < tuples.get(1));
    }
    @Test
    void payloadDoesNotAffectDominanceButIsPreserved() {
        long tuple1 = PackedCriteria.pack(480, 2, 0xCAFE_BABE);
        long tuple2 = PackedCriteria.pack(480, 2, 0xDEAD_BEEF);

        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(tuple1);
        builder.add(tuple2); // Doit être ignoré car dominé/égal

        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertEquals(0xCAFE_BABE, PackedCriteria.payload(front.get(480, 2)));
    }

    @Test
    void withPayloadUpdatesOnlyPayload() {
        long original = PackedCriteria.pack(600, 3, 0);
        long modified = PackedCriteria.withPayload(original, 0x1234_5678);

        assertEquals(0x1234_5678, PackedCriteria.payload(modified));
        assertEquals(PackedCriteria.arrMins(original), PackedCriteria.arrMins(modified));
        assertEquals(PackedCriteria.changes(original), PackedCriteria.changes(modified));
    }

    @Test
    void boundaryArrivalTimesAreHandled() {
        long minArrival = PackedCriteria.pack(-240, 127, 0); // 4h avant minuit
        long maxArrival = PackedCriteria.pack(2879, 0, 0); // 47h59

        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(minArrival);
        builder.add(maxArrival);

        ParetoFront front = builder.build();
        assertEquals(2, front.size());
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.pack(-241, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.pack(2880, 0, 0));
    }

    @Test
    void boundaryChangesAreHandled() {
        long zeroChanges = PackedCriteria.pack(480, 0, 0);
        long maxChanges = PackedCriteria.pack(480, 127, 0);

        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(zeroChanges);
        builder.add(maxChanges);

        assertEquals(1, builder.build().size());
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.pack(480, 128, 0));
    }
    @Test
    void mixingDepAndNoDepThrowsOnDominanceCheck() {
        long noDep = PackedCriteria.pack(480, 2, 0);
        long withDep = PackedCriteria.withDepMins(noDep, 300); // 5h

        ParetoFront.Builder b1 = new ParetoFront.Builder();
        b1.add(noDep);

        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b2.add(withDep);

        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.dominatesOrIsEqual(noDep, withDep));
    }
    @Test
    void addingDepTimeToNonDepTuple() {
        long noDep = PackedCriteria.pack(600, 1, 0);
        long withDep = PackedCriteria.withDepMins(noDep, 720); // 12h

        assertTrue(PackedCriteria.hasDepMins(withDep));
        assertEquals(720, PackedCriteria.depMins(withDep));
        assertEquals(600, PackedCriteria.arrMins(withDep));
    }

    @Test
    void fullyDominatesConsidersDepTimeAdjustment() {
        // Builder1 : tuples sans heure de départ
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        b1.add(PackedCriteria.pack(480, 2, 0)); // Devient (720, 480, 2)
        b1.add(PackedCriteria.pack(600, 1, 0));

        // Builder2 : tuples à dominer
        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b2.add(PackedCriteria.pack(500, 3, 0));
        b2.add(PackedCriteria.pack(650, 2, 0));

        assertThrows(IllegalArgumentException.class, () -> b1.fullyDominates(b2, 0)); // 720 = départ à 12h
    }

    @Test
    void fullyDominatesFailsWhenOneTupleNotDominated() {
        ParetoFront.Builder b1 = new ParetoFront.Builder();
        b1.add(PackedCriteria.withDepMins(PackedCriteria.pack(600, 2, 0), 720));

        ParetoFront.Builder b2 = new ParetoFront.Builder();
        b2.add(PackedCriteria.pack(500, 1, 0));

        assertFalse(b1.fullyDominates(b2, 720));
    }

    @Test
    void frontRemainsImmutableAfterBuilderChanges() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(PackedCriteria.pack(480, 1, 0));
        ParetoFront front1 = builder.build();

        builder.add(PackedCriteria.pack(600, 0, 0));
        ParetoFront front2 = builder.build();

        assertEquals(1, front1.size());
        assertEquals(2, front2.size());
    }

    @Test
    void sameCriteriaDifferentPayloadDoNotDuplicate() {
        long t1 = PackedCriteria.pack(480, 2, 0xAAAA);
        long t2 = PackedCriteria.pack(480, 2, 0xBBBB);

        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(t1);
        builder.add(t2);

        assertEquals(1, builder.build().size());
        assertEquals(0xAAAA, PackedCriteria.payload(builder.build().get(480, 2)));
    }


    }






