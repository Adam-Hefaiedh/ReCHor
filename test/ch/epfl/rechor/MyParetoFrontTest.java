package ch.epfl.rechor;

import ch.epfl.rechor.journey.PackedCriteria;
import ch.epfl.rechor.journey.ParetoFront;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.journey.PackedCriteria.pack;
import static ch.epfl.rechor.journey.PackedCriteria.withoutDepMins;

import static org.junit.jupiter.api.Assertions.*;

class MyParetoFrontTest {

    @Test
    void builderIsEmptyInitially() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        assertTrue(builder.isEmpty());
    }

    @Test
    void builderAddIncreasesSize() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        assertFalse(builder.isEmpty());
    }

    @Test
    void builderClearResetsSize() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.clear();
        assertTrue(builder.isEmpty());
    }

    @Test
    void builderAddAllCombinesCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 1);
        builder1.add(15, 1, 1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(20, 0, 1);

        builder1.addAll(builder2);
        ParetoFront paretoFront = builder1.build();

        assertEquals(3, paretoFront.size());
    }


    @Test
    void addAllHandlesPartiallyDominatedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 1); // (arrMins=10, changes=2, payload=1)
        builder1.add(15, 1, 1); // (arrMins=15, changes=1, payload=1)

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(12, 3, 1); // (arrMins=12, changes=3, payload=1) => Dominé par (10,2,1)
        builder2.add(14, 1, 1); // (arrMins=14, changes=1, payload=1) => Non dominé

        builder1.addAll(builder2);
        ParetoFront paretoFront = builder1.build();

        assertEquals(2, paretoFront.size()); // Doit contenir (10,2,1), (14,1,1)
    }



    @Test
    void paretoFrontGetReturnsCorrectCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 0);
        ParetoFront paretoFront = builder.build();

        long criteria = paretoFront.get(10, 2);
        assertEquals(pack(10, 2, 0), criteria);
    }

    @Test
    void paretoFrontGetThrowsExceptionForNonExistentCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        ParetoFront paretoFront = builder.build();

        assertThrows(NoSuchElementException.class, () -> paretoFront.get(15, 1));
    }


    @Test
    void builderAddDoesNotAddDominatedCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(12, 3, 1); // Dominated by the first criteria
        assertEquals(1, builder.build().size());
    }

    @Test
    void builderFullyDominatesReturnsFalseWhenNotAllCriteriaDominated2() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = PackedCriteria.withDepMins(pack(10, 2, 1), 5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(5, 1, 1));  // (arrMins=5, changes=1, payload=1) => Plus optimal

        assertFalse(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsFalseWhenNotAllCriteriaDominated() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = pack(10, 2, 1);
        criteria1 = PackedCriteria.withDepMins(criteria1,5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(5, 1, 1);  // (arrMins=5, changes=1, payload=1) => Plus optimal

        assertFalse(builder1.fullyDominates(builder2, 5)); //
    }

    @Test
    void builderAddDoesNotAddDuplicateCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(10, 2, 1); // Critère identique

        assertEquals(1, builder.build().size());
    }

    @Test
    void builderDoesNotAddDominatedCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(15, 3, 1); // Dominé par (10, 2, 1)

        assertEquals(1, builder.build().size());
    }

    @Test
    void builderMergeNonDominatedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(8, 3, 1); // Non dominé par (10, 2, 1)

        builder1.addAll(builder2);
        ParetoFront paretoFront = builder1.build();

        assertEquals(2, paretoFront.size());
    }

    ////////////////////////////////////////////////////////////////////

    @Test
    void builderFullyDominatesReturnsTrueWhenAllCriteriaDominated() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = PackedCriteria.withDepMins(pack(10, 2, 1), 5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(15, 3, 1));  // (arrMins=15, changes=3, payload=1) => Moins optimal

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true
    }

    @Test
    void builderFullyDominatesReturnsFalseWhenCriteriaEqual() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = PackedCriteria.withDepMins(pack(10, 2, 1), 5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(PackedCriteria.withDepMins(pack(10, 2, 1), 5));  // Critères identiques

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsFalseWhenNoCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsTrueWithMultipleCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(PackedCriteria.withDepMins(pack(10, 2, 1), 5));
        builder1.add(PackedCriteria.withDepMins(pack(15, 1, 1), 5));

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(20, 3, 1));  // (arrMins=20, changes=3, payload=1) => Moins optimal

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true
    }

    @Test
    void builderFullyDominatesReturnsFalseWithMixedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(PackedCriteria.withDepMins(pack(10, 2, 1), 5));
        builder1.add(PackedCriteria.withDepMins(pack(20, 1, 1), 5));

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(15, 3, 1));  // (arrMins=15, changes=3, payload=1) => Moins optimal
        builder2.add(pack(5, 1, 1));   // (arrMins=5, changes=1, payload=1) => Plus optimal

        assertFalse(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsTrueWithDominatedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = pack(10, 2, 1);
        long criteria2 = pack(30, 5, 1);

        criteria1 = PackedCriteria.withDepMins(criteria1,5);
        criteria2 = PackedCriteria.withDepMins(criteria1,5);

        builder1.add(criteria1);
        builder1.add(criteria2);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        builder2.add(pack(20, 3, 1));  // (arrMins=20, changes=3, payload=1) => Moins optimal
        builder2.add(pack(25, 4, 1));
        builder2.add(pack(21, 2,1));// (arrMins=25, changes=4, payload=1) => Moins optimal

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true


    }
    @Test
    void fullyDominatesHandlesEmptyFrontier() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder(); // Frontière vide

        assertTrue(builder1.fullyDominates(builder2, 5)); // Une frontière non vide domine toujours une frontière vide
        assertFalse(builder2.fullyDominates(builder1, 5)); // Une frontière vide ne domine jamais une frontière non vide
    }




    @Test
    void builderReturnTrueWhenNoCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    @Test
    void builderFullyDominatesReturnsFalseWithNonDominatedCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = pack(10, 2, 1);
        long criteria2 = pack(30, 5, 1);

        criteria1 = PackedCriteria.withDepMins(criteria1, 5);
        criteria2 = PackedCriteria.withDepMins(criteria2, 5);

        builder1.add(criteria1);
        builder1.add(criteria2);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(pack(5, 1, 1));   // (arrMins=5, changes=1, payload=1) => Plus optimal
        builder2.add(pack(25, 4, 1));  // (arrMins=25, changes=4, payload=1) => Moins optimal

        assertFalse(builder1.fullyDominates(builder2, 5)); // Devrait être false
    }

    @Test
    void builderFullyDominatesReturnsTrueWithEqualCriteria() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long criteria1 = pack(10, 2, 1);
        criteria1 = PackedCriteria.withDepMins(criteria1, 5);
        builder1.add(criteria1);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(PackedCriteria.withDepMins(pack(10, 2, 1), 5));  // Critères identiques

        assertTrue(builder1.fullyDominates(builder2, 5)); // Devrait être true
    }

    @Test
    void builderIsEmptyReturnsTrueWhenEmpty() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        assertTrue(builder.isEmpty());
    }

    @Test
    void builderIsEmptyReturnsFalseWhenNotEmpty() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        assertFalse(builder.isEmpty());
    }

    @Test
    void builderClearEmptiesTheBuilder() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.clear();
        assertTrue(builder.isEmpty());
    }


    @Test
    void builderBuildCreatesParetoFront1() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(10, 2, 1);
        builder.add(10, 2, 1);
        builder.add(7,2,1);
        builder.add(9, 3, 1);

        ParetoFront paretoFront2 = builder.build();
        System.out.println("1: "+paretoFront2);
        assertEquals(1, paretoFront2.size());
    }


    @Test
    void clearWorksAfterMultipleAdds() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(15, 1, 1);
        builder.clear();
        builder.add(20, 0, 1);

        ParetoFront paretoFront = builder.build();
        assertEquals(1, paretoFront.size()); // Doit contenir uniquement (20,0,1)
    }


    @Test
    void builderBuildCreatesParetoFront2() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(10, 2, 1);
        builder.add(10, 2, 1);
        builder.add(9, 3, 1);
        builder.add(7,2,1);

        ParetoFront paretoFront2 = builder.build();
        System.out.println("2: "+paretoFront2);
        assertEquals(1, paretoFront2.size());
    }


    @Test
    void buildRemovesDominatedCriteriaInMiddle() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1); // (arrMins=10, changes=2, payload=1)
        builder.add(12, 3, 1); // (arrMins=12, changes=3, payload=1) => Dominé par (10,2,1)
        builder.add(15, 1, 1); // (arrMins=15, changes=1, payload=1)

        ParetoFront paretoFront = builder.build();
        assertEquals(2, paretoFront.size()); // Doit contenir (10,2,1) et (15,1,1)
    }


    @Test
    void buildHandlesUnsortedCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(15, 1, 1); // (arrMins=15, changes=1, payload=1)
        builder.add(10, 2, 1); // (arrMins=10, changes=2, payload=1)
        builder.add(20, 0, 1); // (arrMins=20, changes=0, payload=1)

        ParetoFront paretoFront = builder.build();
        assertEquals(3, paretoFront.size()); // Doit contenir (10,2,1), (15,1,1), et (20,0,1)
    }


    @Test
    void forEachDoesNotAllowModifyingCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(15, 1, 1);

        ParetoFront paretoFront = builder.build();
        List<Long> criteriaList = new ArrayList<>();
        paretoFront.forEach(criteria -> {
            criteriaList.add(criteria);
            // Tentative de modification (ne devrait pas avoir d'effet)
            criteria = pack(20, 0, 1);
        });

        assertEquals(2, criteriaList.size()); // Doit contenir les critères originaux
        assertTrue(criteriaList.contains(pack(10, 2, 1)));
        assertTrue(criteriaList.contains(pack(15, 1, 1)));
    }

    @Test
    void builderHandlesInvertedDominance() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 3, 1); // (arrMins=10, changes=3, payload=1)
        builder.add(12, 2, 1); // (arrMins=12, changes=2, payload=1) => Moins bon sur l'heure, meilleur sur les changements

        ParetoFront paretoFront = builder.build();
        assertEquals(2, paretoFront.size()); // Les deux critères doivent être conservés car aucun ne domine l'autre
    }


    @Test
    void builderHandlesDifferentDepartureTimes() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(PackedCriteria.withDepMins(pack(10, 2, 1), 5)); // Départ à 5h
        builder.add(PackedCriteria.withDepMins(pack(10, 2, 1), 6)); // Départ à 6h

        ParetoFront paretoFront = builder.build();
        assertEquals(1, paretoFront.size()); // Les deux critères doivent être conservés car les heures de départ diffèrent
    }
    @Test
    void builderHandlesCriteriaWithoutDepartureTime() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(PackedCriteria.withoutDepMins(pack(10, 2, 1))); // (arrMins=10, changes=2, payload=1)
        builder.add(PackedCriteria.withoutDepMins(pack(12, 3, 1))); // (arrMins=12, changes=3, payload=1) => Dominé par (10,2,1)
        builder.add(PackedCriteria.withoutDepMins(pack(8, 2, 1)));  // (arrMins=8, changes=2, payload=1) => Domine (10,2,1)

        ParetoFront paretoFront = builder.build();
        System.out.println(builder);
        assertEquals(1, paretoFront.size()); // Seul (8,2,1) doit rester
        assertTrue(paretoFront.get(8, 2) != 0);
    }
    @Test
    void builderHandlesCriteriaWithDifferentPayload() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(PackedCriteria.pack(10, 2, 1)); // (arrMins=10, changes=2, payload=1)
        builder.add(PackedCriteria.pack(10, 2, 2)); // Mêmes critères, mais charge utile différente

        ParetoFront paretoFront = builder.build();
        assertEquals(1, paretoFront.size()); // Un seul critère doit rester
        assertTrue(paretoFront.get(10, 2) != 0);
    }

    @Test
    void builderHandlesEqualCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(PackedCriteria.pack(10, 2, 1)); // (arrMins=10, changes=2, payload=1)
        builder.add(PackedCriteria.pack(10, 2, 1)); // Critère identique

        ParetoFront paretoFront = builder.build();
        assertEquals(1, paretoFront.size()); // Un seul critère doit rester
        assertTrue(paretoFront.get(10, 2) != 0);
    }
    @Test
    void builderHandlesFullCriteriaDominance() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(PackedCriteria.pack(10, 2, 1)); // (arrMins=10, changes=2, payload=1)
        builder.add(PackedCriteria.pack(12, 3, 1)); // (arrMins=12, changes=3, payload=1) => Dominé par (10,2,1)
        builder.add(PackedCriteria.pack(8, 2, 1));  // (arrMins=8, changes=2, payload=1) => Domine (10,2,1)

        ParetoFront paretoFront = builder.build();
        System.out.println(builder);
        assertEquals(1, paretoFront.size()); // Seul (8,2,1) doit rester
        assertTrue(paretoFront.get(8, 2) != 0);
    }

    @Test
    void getThrowsExceptionForNonExistentCriteria() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 1);
        builder.add(15, 1, 1);

        ParetoFront paretoFront = builder.build();
        assertThrows(NoSuchElementException.class, () -> paretoFront.get(20, 0)); // Critère non existant
    }
















}