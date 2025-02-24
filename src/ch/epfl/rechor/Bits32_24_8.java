package ch.epfl.rechor;

public final class Bits32_24_8 {

    public static int pack(int bits24, int bits8){
        Preconditions.checkArgument(bits24 >> 24==0|| bits8 >> 8==0);
        return (bits24 << 8) | bits8;
    }
    public static int unpack24(int bits32){
        return bits32 >> 8;
    }
    public static int unpack8(int bits32){
        return bits32 & 255;
    }

}
