package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

public final class PackedCriteria {
    public static long pack(int arrMins, int changes, int payload){
        Preconditions.checkArgument(arrMins >= -240 && arrMins < 2880);
        Preconditions.checkArgument(changes >> 7 == 0);
        return (long) arrMins << 38 | (long) changes << 31 | payload;
    }
    public static boolean hasDepMins(long criteria)
    {
        return criteria >> 50 != 0;
    }
    public static int depMins(long criteria){
        Preconditions.checkArgument(hasDepMins(criteria));
        return (int) (criteria >> 50);
    }
    public static int arrMins(long criteria)
    {
        return (int) criteria >> 38 & 4095;
    }
    public static int changes(long criteria)
    {
        return (int) criteria >> 31 & 127;
    }
    public static int payload(long criteria){
        return (int) ((int) criteria & 0xFFFFFFFFL);
    }
    public static boolean dominatesOrIsEqual(long criteria1, long criteria2){
        int avantage1 = 0;
        int avantage2 = 0;
        Preconditions.checkArgument(hasDepMins(criteria1) == hasDepMins(criteria2));
        int[][] values = {
                {depMins(criteria1), depMins(criteria2)},
                {arrMins(criteria1), arrMins(criteria2)},
                {changes(criteria1), changes(criteria2)}
        };

        for (int[] pair : values) {
            if (pair[0] < pair[1]) {
                avantage1++;
            } else if (pair[1] < pair[0]) {
                avantage2++;
            }
        }

        return avantage1 >= avantage2;
    }
    public static long withoutDepMins(long criteria){
        return criteria & 0x1FFFFFFFFFFFFL;
    }
    public static long withDepMins(long criteria, int depMins1)
    {
        return withoutDepMins(criteria) | depMins1 << 50;
    }
    public static long withAdditionalChange(long criteria){
        
    }
}
