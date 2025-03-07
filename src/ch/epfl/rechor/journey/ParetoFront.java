package ch.epfl.rechor.journey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static java.lang.System.arraycopy;


public class ParetoFront {
    private long[] tuples;
    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);

    public static void main(String[] args) {
        Builder build = new Builder();
        build.add(pack(480,3,2));
        build.add(pack(480,4,22));
        build.add(pack(481,2,23));
        build.add(pack(482,1,25));
        build.add(pack(483,0,23));
        build.add(pack(484,1,21));
        ParetoFront paretoFront = build.build();
        for(long par : paretoFront.tuples){
            System.out.println(arrMins(par));
            System.out.println(changes(par));
        }
    }
    private ParetoFront(long[] tuples){
        this.tuples = tuples;
    }

    public int size(){
        return tuples.length;
    }
    public long get(int arrMins, int changes)
    {
        for(long critere : tuples){
            if(PackedCriteria.arrMins(critere) == arrMins && PackedCriteria.changes(critere) == changes){
                return critere;
            }
        }
        throw new NoSuchElementException();
    }
    public void forEach(LongConsumer action){
        for(long critere : tuples){
            action.accept(critere);
        }
    }

    public static class Builder{
        private long[] tuples;
        private int capacity;

        public Builder(){
            tuples = new long[0];
            capacity = 0;
        }
        public Builder(Builder that) {
            tuples = that.tuples;
            capacity = that.tuples.length;
        }

        public boolean isEmpty(){
            for(long critere : tuples){
                if(critere !=0)return false;
            }
            return true;
        }
        public Builder clear(){
            Arrays.fill(tuples, 0);
            return this;
        }
        public Builder add(long packedTuple){
            int pos = 0;
            int dest = 0;
            if(tuples.length == 0)
            {
                tuples = new long[++capacity];
                tuples[capacity-1] = packedTuple;
                return this;
            }
            for(int i=0; i<tuples.length; i++){
                if(dominatesOrIsEqual(tuples[i],packedTuple))return this;
                if (dominatesOrIsEqual(packedTuple,tuples[i])) continue;
                if (dest != i) tuples[dest] = tuples[i];
                dest += 1;
                if(withPayload(tuples[i],0)< withPayload(packedTuple,0))pos++;
            }
            long[] tuples2 = new long[dest+1];
            arraycopy(tuples,0,tuples2,0,pos);
            arraycopy(tuples,pos,tuples2,pos+1, dest-pos);

            tuples2[pos] = packedTuple;
            capacity = dest+1;
            tuples = tuples2;
            return this;
        }
        public Builder add(int arrMins, int changes, int payload){
            return add(pack(arrMins,changes,payload));
        }
        public Builder addAll(Builder that){
            for(long critere : that.tuples){
                add(critere);
            }
            return this;
        }
        public boolean fullyDominates(Builder that, int depMins){
            boolean dominated = false;
            for(long critere : tuples){
                PackedCriteria.withDepMins(critere, depMins);
                for(long critere2 : that.tuples){
                    if(!PackedCriteria.dominatesOrIsEqual(critere,critere2))dominated = true;
                }
                if(!dominated)return true;
            }
            return false;
        }
        public void forEach(LongConsumer action){
            for(long critere : tuples){
                action.accept(critere);
            }
        }
        public ParetoFront build(){
            return new ParetoFront(tuples);
        }
    }
}
