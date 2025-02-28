package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyPackedCriteriaTest {

    public static void printLongBits(long value) {
        StringBuilder binary = new StringBuilder(64);

        for (int i = 63; i >= 0; i--) {
            if ((value & (1L << i)) != 0) {
                binary.append('1');
            } else {
                binary.append('0');
            }

            if (i % 8 == 0 && i > 0) {
                binary.append(' ');
            }
        }

        System.out.println("Binary representation: " + binary.toString());
    }

    @Test
    void basicTestPackingUnpackingData(){
        long critere1 = PackedCriteria.pack(540, 2, 2025);
        System.out.println("Critère 1: " + critere1);
        printLongBits(critere1);
        assertEquals(540, PackedCriteria.arrMins(critere1));
        assertFalse(PackedCriteria.hasDepMins(critere1));
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.depMins(critere1));

        long critere1_2 = PackedCriteria.withDepMins(critere1, 480);
        System.out.println("Critère 2: " + critere1_2);
        printLongBits(critere1_2);
        assertEquals(0b01101001_01111001_10000110_00000010_00000000_00000000_00000111_11101001L, critere1_2);
        assertTrue(PackedCriteria.hasDepMins(critere1_2));
        assertEquals(480, PackedCriteria.depMins(critere1_2));
        assertEquals(540, PackedCriteria.arrMins(critere1_2));
        assertEquals(2, PackedCriteria.changes(critere1_2));

        long critere1_3 = PackedCriteria.withAdditionalChange(critere1);
        System.out.println("Critère 3: " + critere1_3);
        printLongBits(critere1_3);
        assertEquals(3, PackedCriteria.changes(critere1_3));


        long critere1_4 = PackedCriteria.withPayload(critere1_2, 2024);
        System.out.println("Critère 4: " + critere1_4);
        printLongBits(critere1_4);
        assertEquals(2024, PackedCriteria.payload(critere1_4));

        long critere1_5 = PackedCriteria.withoutDepMins(critere1_4);
        System.out.println("Critère 4: " + critere1_4);
        printLongBits(critere1_4);
        assertFalse(PackedCriteria.hasDepMins(critere1_5));

        assertThrows(IllegalArgumentException.class, () ->PackedCriteria.dominatesOrIsEqual(critere1, critere1_2));
        assertTrue(PackedCriteria.dominatesOrIsEqual(critere1_2, critere1_4));

        long critere1_6 = PackedCriteria.pack(540, 2, 2025);
        long critere1_7 = PackedCriteria.pack(550, 2, 2025);
        assertTrue(PackedCriteria.dominatesOrIsEqual(critere1_6, critere1_7));

        PackedCriteria.withDepMins(critere1_6, 300);
        PackedCriteria.withDepMins(critere1_7, 310);
        assertTrue(PackedCriteria.dominatesOrIsEqual(critere1_6, critere1_7));
        assertFalse(PackedCriteria.dominatesOrIsEqual(critere1_7, critere1_6));

    }

    @Test
    void edgeCaseTesting(){

        long critere1 = PackedCriteria.pack(-240, 126, ~0);
        System.out.println(critere1);
        PackedCriteria.withAdditionalChange(critere1);
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.withDepMins(critere1, 2880));
        long critere1_2 = PackedCriteria.withDepMins(critere1, 2879);

        assertEquals(2879, PackedCriteria.depMins(critere1_2));
        assertEquals(-240, PackedCriteria.arrMins(critere1_2));

        long critere1_3 = PackedCriteria.withoutDepMins(critere1_2);
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.depMins(critere1_3));
        

    }


}