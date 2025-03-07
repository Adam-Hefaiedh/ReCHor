package ch.epfl.rechor.journey;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static java.lang.System.arraycopy;
import static java.util.List.copyOf;

/**
 * La classe représente une frontière de Pareto de critères d'optimisation.
 */
public class ParetoFront {
    private final long[] tuples;
    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);

    private ParetoFront(long[] tuples){
        this.tuples = tuples;
    }

    /**
     * retourne la taille de la frontière de Pareto
     * @return le nombre de tuples (paires ou triplets de critères d'optimisation) qu'elle contient
     */
    public int size(){
        return tuples.length;
    }

    /**
     * retourne les critères d'optimisation empaquetés dont
     * l'heure d'arrivée et le nombre de changements sont ceux donnés
     * @param arrMins
     * L'heure d'arrivée du critères a trouver
     * @param changes
     * Le nombre de changements du critères a trouver
     * @return le critères contenant cette heure d'arrivée et ce nombre de changement
     */
    public long get(int arrMins, int changes)
    {
        for(long critere : tuples){
            if(PackedCriteria.arrMins(critere) == arrMins && PackedCriteria.changes(critere) == changes){
                return critere;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * appelle la méthode accept de l'action de type LongConsumer donnée
     * avec chacun des critères de la frontière, dans l'ordre.
     * @param action
     * L'action a effectuer pour chaque critères
     */
    public void forEach(LongConsumer action){
        for(long critere : tuples){
            action.accept(critere);
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(long critere : tuples){
            sb.append("Heure d'arrivée: \n").append(arrMins(critere)).append("\n");
            sb.append("Nombre de changement: \n").append(changes(critere)).append("\n");
        }
        return sb.toString();
    }

    /**
     * La classe représente un bâtisseur de frontière de Pareto
     */
    public static class Builder{
        private long[] tuples_build;
        private int capacity;
        private int size_effective;

        /**
         * retourne un bâtisseur dont la frontière en cours de construction est vide
         */
        public Builder(){
            tuples_build = new long[2];
            capacity = 2;
            size_effective = 0;
        }

        /**
         * retourne un nouveau bâtisseur avec les
         * mêmes attributs que celui reçu en argument
         * @param that
         * Bâtisseur a copier
         **/
        public Builder(Builder that) {
            tuples_build = that.tuples_build;
            capacity = that.capacity;
            size_effective = that.size_effective;
        }

        /**
         * retourne vrai si et seulement si la frontière en cours de construction est vide
         * @return Si le tableau tuple est vide
         */
        public boolean isEmpty(){
            return size_effective == 0;
        }

        /**
         * vide la frontière en cours de construction en supprimant tous ses éléments
         * @return Le builder courant
         */
        public Builder clear(){
            tuples_build = EMPTY.tuples;
            size_effective = 0;
            capacity = 0;
            return this;
        }

        /**
         * Ajoute à la frontière le tuple de critères empaquetés donné
         * @param packedTuple
         * Le tuple de critères empaquetés a ajouté
         * @return Le builder courant
         */
        public Builder add(long packedTuple){
            int pos = -1;
            int size = 0;
            while(size != size_effective && pos == -1)
            {
                if(withPayload(tuples_build[size],Integer.MAX_VALUE) <= withPayload(packedTuple,Integer.MAX_VALUE)){
                    if(dominatesOrIsEqual(tuples_build[size], packedTuple)){
                        return this;
                    }
                    size++;
                }
                else pos = size;
            }
            if(size == size_effective){
                if(size_effective == capacity){
                    long[] tuples_build1 = new long[capacity*3/2 +1];
                    arraycopy(tuples_build,0,tuples_build1,0,capacity);
                    capacity++;
                    tuples_build = tuples_build1;
                }
                tuples_build[size_effective] = packedTuple;
                size_effective++;
                return this;
            }

            int dest = pos;
            for(int i=pos; i<size_effective; i++) {
                if (dominatesOrIsEqual(packedTuple, tuples_build[i])){
                    continue;
                }
                if (dest != i) tuples_build[dest] = tuples_build[i];
                dest += 1;
            }

            long[] tuples_build2 = new long[dest+1];
            arraycopy(tuples_build,0,tuples_build2,0,pos);
            arraycopy(tuples_build,pos,tuples_build2,pos+1, dest-pos);
            tuples_build2[pos] = packedTuple;
            capacity = dest+1;
            size_effective = capacity;
            tuples_build = tuples_build2;
            return this;
        }

        /**
         *  ajoute à la frontière un tuple de critères augmentés ayant
         *  l'heure d'arrivée, le nombre de changements et la charge utile donnés
         * @param arrMins
         * L'heure d'arrivée donnée
         * @param changes
         * Le nombre de changement donnée
         * @param payload
         * La charge utile donnée
         * @return Le builder courant
         */
        public Builder add(int arrMins, int changes, int payload){
            return add(pack(arrMins,changes,payload));
        }

        /**
         * Ajoute à la frontière tous les tuples présents dans
         * la frontière en cours de construction par le bâtisseur donné
         * @param that
         * Le bâtisseur donné
         * @return Le builder courant
         */
        public Builder addAll(Builder that){
            for(int i=0; i<that.size_effective; i++){
                add(that.tuples_build[i]);
            }
            return this;
        }

        /**
         *
         * @param that
         * @param depMins
         * @return
         */
        public boolean fullyDominates(Builder that, int depMins){
            int domination;
            long deps;
            if(that.size_effective == 0)return true;
           for(int i=0; i<this.size_effective;i++){
               domination = 0;
               for(int j=0; j<that.size_effective;j++)
               {
                   deps = withDepMins(that.tuples_build[j], depMins);
                   if(dominatesOrIsEqual(this.tuples_build[i],deps))domination++;
               }
               if(domination == that.size_effective) return true;
               
           }
           return false;
        }

        /**
         * appelle la méthode accept de l'action de type LongConsumer donnée
         * avec chacun des critères de la frontière, dans l'ordre.
         * @param action
         * L'action a effectuer pour chaque critères
         */
        public void forEach(LongConsumer action){
            for(long critere : tuples_build){
                action.accept(critere);
            }
        }
        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<size_effective;i++){
                sb.append("Heure d'arrivée: \n").append(arrMins(tuples_build[i])).append("\n");
                sb.append("Nombre de changement: \n").append(changes(tuples_build[i])).append("\n");
            }
            return sb.toString();
        }

        /**
         * Retourne la frontière de Pareto en cours de construction par ce bâtisseur.
         * @return la frontière de Pareto en cours de construction par ce bâtisseur
         */
        public ParetoFront build(){
            long[] tuples_build1 = new long[size_effective];
            arraycopy(tuples_build,0,tuples_build1,0,size_effective);
            return new ParetoFront(tuples_build1);
        }
    }
}
