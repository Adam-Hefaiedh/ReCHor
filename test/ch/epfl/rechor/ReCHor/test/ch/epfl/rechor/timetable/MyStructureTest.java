package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyStructureTest {

    // Teste que la taille totale (en octets) est correctement calculée
    @Test
    void testTailleTotale() {
        Structure.Field f0 = Structure.field(0, Structure.FieldType.U8);
        Structure.Field f1 = Structure.field(1, Structure.FieldType.U16);
        Structure.Field f2 = Structure.field(2, Structure.FieldType.S32);
        Structure structure = new Structure(f0, f1, f2);
        assertEquals(7, structure.totalSize(), "La taille totale doit être 7 octets.");
    }

    @Test
    void testOffsetElement0() {
        Structure.Field f0 = Structure.field(0, Structure.FieldType.U8);
        Structure.Field f1 = Structure.field(1, Structure.FieldType.U16);
        Structure.Field f2 = Structure.field(2, Structure.FieldType.S32);
        Structure structure = new Structure(f0, f1, f2);
        assertEquals(0, structure.offset(0, 0), "L'offset du champ 0 pour l'élément 0 doit être 0.");
        assertEquals(1, structure.offset(1, 0), "L'offset du champ 1 pour l'élément 0 doit être 1.");
        assertEquals(3, structure.offset(2, 0), "L'offset du champ 2 pour l'élément 0 doit être 3.");
    }

    // Teste le calcul des décalages pour un enregistrement d'indice supérieur (ex. indice 2)
    @Test
    void testOffsetElement2() {
        Structure.Field f0 = Structure.field(0, Structure.FieldType.U8);
        Structure.Field f1 = Structure.field(1, Structure.FieldType.U16);
        Structure.Field f2 = Structure.field(2, Structure.FieldType.S32);
        Structure structure = new Structure(f0, f1, f2);
        assertEquals(14, structure.offset(0, 2), "L'offset du champ 0 pour l'élément 2 doit être 14.");
        assertEquals(15, structure.offset(1, 2), "L'offset du champ 1 pour l'élément 2 doit être 15.");
        assertEquals(17, structure.offset(2, 2), "L'offset du champ 2 pour l'élément 2 doit être 17.");
    }

    // Vérifie que le constructeur lève une exception si les champs ne sont pas fournis dans l'ordre attendu
    @Test
    void testIndicesIncohérents() {
        Structure.Field f0 = Structure.field(0, Structure.FieldType.U8);
        Structure.Field f1 = new Structure.Field(2, Structure.FieldType.U16);
        assertThrows(IllegalArgumentException.class, () -> new Structure(f0, f1),
                "Le constructeur doit lever une IllegalArgumentException si les indices ne sont pas dans l'ordre.");
    }

    // Vérifie que la création d'un champ avec un type null lève une NullPointerException
    @Test
    void testChampAvecTypeNull() {
        assertThrows(NullPointerException.class, () -> new Structure.Field(0, null),
                "La création d'un champ avec un type null doit lever une NullPointerException.");
    }

    // Test de la méthode statique field pour créer un champ
    @Test
    void testFabriqueDeChamp() {
        Structure.Field champ = Structure.field(0, Structure.FieldType.S32);
        assertNotNull(champ, "Le champ créé ne doit pas être nul.");
        assertEquals(0, champ.index(), "L'indice du champ doit être 0.");
        assertEquals(Structure.FieldType.S32, champ.type(), "Le type du champ doit être S32.");
    }

    // Vérifie que la méthode offset lève IndexOutOfBoundsException pour un indice de champ invalide
    @Test
    void testOffsetIndiceChampInvalide() {
        Structure.Field f0 = Structure.field(0, Structure.FieldType.U8);
        Structure.Field f1 = Structure.field(1, Structure.FieldType.S32);
        Structure structure = new Structure(f0, f1);
        assertThrows(IndexOutOfBoundsException.class, () -> structure.offset(2, 0),
                "Un indice de champ invalide doit lever une IndexOutOfBoundsException.");
    }

    // Teste une structure ne comportant qu'un seul champ
    @Test
    void testStructureAvecUnSeulChamp() {
        Structure.Field f0 = Structure.field(0, Structure.FieldType.U16);
        Structure structure = new Structure(f0);
        assertEquals(2, structure.totalSize(), "La taille totale doit être 2 octets pour un seul champ U16.");
        assertEquals(2, structure.offset(0, 1), "L'offset du seul champ pour l'élément 1 doit être 2.");
    }
}