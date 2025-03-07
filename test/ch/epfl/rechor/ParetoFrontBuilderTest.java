package ch.epfl.rechor;

import ch.epfl.rechor.journey.PackedCriteria;
import ch.epfl.rechor.journey.ParetoFront;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;
import static ch.epfl.rechor.journey.ParetoFront.*;

public class ParetoFrontBuilderTest {

    /** Tests sur la classe Builder **/

    @Test
    public void testBuilderIsInitiallyEmpty() {
        Builder builder = new Builder();
        assertTrue(builder.isEmpty(), "Un Builder initial doit être vide.");
    }

    @Test
    public void testBuilderCopyConstructor() {
        Builder original = new Builder();
        original.add(PackedCriteria.pack(10,2, 0));

        Builder copy = new Builder(original);
        assertEquals(1, copy.build().size(), "Le constructeur de copie doit préserver les éléments.");
        assertEquals(PackedCriteria.pack(10,2, 0), copy.build().get(10, 2));
    }

    @Test
    public void testBuilderAddTuple() {
        Builder builder = new Builder();
        builder.add(PackedCriteria.pack(10,2, 0));

        assertFalse(builder.isEmpty(), "Le Builder ne doit pas être vide après un ajout.");
        assertEquals(1, builder.build().size(), "Un seul élément doit être ajouté.");
    }

    @Test
    public void testBuilderAddDominatedTuple() {
        Builder builder = new Builder();
        builder.add(PackedCriteria.pack(10,2, 0));  // Ajout initial
        builder.add(PackedCriteria.pack(12,3, 0));  // Devrait être conservé
        builder.add(PackedCriteria.pack(9,1, 0));   // Devrait dominer (moins d’arrêts et temps inférieur)

        assertEquals(1, builder.build().size(), "Seul le tuple dominant doit être conservé.");
        assertEquals(PackedCriteria.pack(9,1, 0), builder.build().get(9, 1), "Le tuple dominant doit être conservé.");
    }

    @Test
    public void testBuilderClear() {
        Builder builder = new Builder();
        builder.add(PackedCriteria.pack(10,2, 0));
        builder.clear();

        assertTrue(builder.isEmpty(), "Le Builder doit être vide après clear().");
        assertThrows(NoSuchElementException.class, () -> builder.build().get(10, 2), "Une exception doit être levée pour un critère absent.");
    }

    @Test
    public void testBuilderAddAll() {
        Builder builder1 = new Builder();
        builder1.add(PackedCriteria.pack(10,2, 0));
        builder1.add(PackedCriteria.pack(12,3, 0));

        Builder builder2 = new Builder();
        builder2.add(PackedCriteria.pack(8,1, 0));  // Devrait être ajouté

        builder1.addAll(builder2);

        assertEquals(1, builder1.build().size(), "Seuls les éléments non dominés doivent rester.");
        assertEquals(PackedCriteria.pack(8,1, 0), builder1.build().get(8, 1), "L'élément ajouté doit être accessible.");
    }

    @Test
    public void testBuilderFullyDominates() {
        ///  Builder 1 n'a pas de min de depart ainsi une exeption est levee !
        Builder builder1 = new Builder();
        builder1.add(PackedCriteria.pack(10,2, 0));
        builder1.add(PackedCriteria.pack(12,3, 0));

        Builder builder2 = new Builder();
        builder2.add(PackedCriteria.pack(15,4, 0)); // Plus lent et plus de changements = dominé
        ///  ajouter AssertThrows
    }

    @Test
    public void testBuilderDoesNotFullyDominate() {
        ///  Builder 1 n'a pas de min de depart ainsi une exeption est levee !
        Builder builder1 = new Builder();
        builder1.add(PackedCriteria.pack(0,10, 2));

        Builder builder2 = new Builder();
        builder2.add(PackedCriteria.pack(0,8, 1)); // Meilleur, donc pas dominé

        ///  ajouter AssertThrows
    }

    @Test
    public void testBuilderBuildCreatesParetoFront() {
        Builder builder = new Builder();
        builder.add(PackedCriteria.pack(8,3, 0));
        builder.add(PackedCriteria.pack(8,4, 0));
        builder.add(PackedCriteria.pack(7,4, 0));

        ParetoFront front = builder.build();
        System.out.println(builder);
        assertEquals(2, front.size(), "Le ParetoFront construit doit contenir tous les éléments.");
        //assertEquals(PackedCriteria.pack(7,4, 0), front.get(7, 4), "Les éléments doivent être correctement insérés.");
    }

    /** Tests sur la classe ParetoFront **/

    @Test
    public void testParetoFrontSize() {
        Builder builder = new Builder();
        builder.add(PackedCriteria.pack(10,2, 0));
        builder.add(PackedCriteria.pack(15,1, 0));
        System.out.println(builder);
        ParetoFront front = builder.build();
        System.out.println(builder);
        assertEquals(2, front.size(), "La taille doit correspondre au nombre d'éléments ajoutés.");
    }

    @Test
    public void testParetoFrontGetValidCriteria() {
        Builder builder = new Builder();
        builder.add(PackedCriteria.pack(10,2, 0));
        builder.add(PackedCriteria.pack(15,1, 0));

        ParetoFront front = builder.build();
        assertEquals(PackedCriteria.pack(10,2, 0), front.get(10, 2), "L'élément 10,2 doit être retrouvé.");
        assertEquals(PackedCriteria.pack(15,1, 0), front.get(15, 1), "L'élément 15,1 doit être retrouvé.");
    }

    @Test
    public void testParetoFrontGetThrowsExceptionOnMissingCriteria() {
        Builder builder = new Builder();
        builder.add(PackedCriteria.pack(10,2, 0));

        ParetoFront front = builder.build();
        assertThrows(NoSuchElementException.class, () -> front.get(12, 3),
                "Une exception doit être levée pour un critère absent.");
    }

//    @Test
//    public void testParetoFrontForEachTraversesAllElements() {
//        Builder builder = new Builder();
//        builder.add(PackedCriteria.pack(0,10, 2));
//        builder.add(PackedCriteria.pack(0,15, 1));
//        builder.add(PackedCriteria.pack(0,20, 3));
//
//        ParetoFront front = builder.build();
//        List<Long> collected = new ArrayList<>();
//
//        front.forEach(collected::add);
//
//        assertEquals(List.of((10, 2), PackedCriteria.pack((0,15, 1), PackedCriteria.pack((20,3, 0)), collected, "forEach doit traverser tous les éléments dans le bon ordre."));
//    }

    @Test
    public void testParetoFrontForEachOnEmptyFront() {
        ParetoFront emptyFront = ParetoFront.EMPTY;
        List<Long> collected = new ArrayList<>();

        emptyFront.forEach(collected::add);

        assertTrue(collected.isEmpty(), "forEach sur une frontière vide ne doit pas appeler l'action.");
    }


}