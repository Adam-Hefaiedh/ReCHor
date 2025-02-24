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
}
