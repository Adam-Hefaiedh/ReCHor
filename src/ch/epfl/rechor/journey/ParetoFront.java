package ch.epfl.rechor.journey;

import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static java.lang.System.arraycopy;

/**
 * La classe représente une frontière de Pareto de critères d'optimisation.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public final class ParetoFront {
    private final long[] tuples;
    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);

    private ParetoFront(long[] tuples){
        this.tuples = tuples;
    }

    /**
     * Retourne la taille de la frontière de Pareto.
     * @return le nombre de tuples (paires ou triplets de critères d'optimisation) qu'elle contient.
     */
    public int size(){
        return tuples.length;
    }

    /**
     * Retourne les critères d'optimisation empaquetés dont
     * l'heure d'arrivée et le nombre de changements sont ceux donnés.
     * @param arrMins L'heure d'arrivée du critère à trouver.
     * @param changes Le nombre de changements du critère à trouver.
     * @return le critère contenant cette heure d'arrivée et ce nombre de changements.
     *
     * @throws NoSuchElementException si les critères en paramètre ne sont pas dans le tableau.
     */
    public long get(int arrMins, int changes)
    {
        for(long criteria : tuples){
            if(PackedCriteria.arrMins(criteria) == arrMins && PackedCriteria.changes(criteria) == changes){
                return criteria;
            }
        }
        throw new NoSuchElementException();
    }


    /**
     * Appelle la méthode accept de l'action de type LongConsumer donnée
     * avec chacun des critères de la frontière, dans l'ordre.
     * @param action L'action a effectué pour chaque critère.
     */
    public void forEach(LongConsumer action){
        for(long criteria : tuples){
            action.accept(criteria);
        }
    }

    /**
     * La classe représente un bâtisseur de frontière de Pareto.
     */
    public static class Builder{
        private long[] tuples_build;
        private int size_effective;
        private static final int MAX_PAYLOAD = 0xFFFFFFFF;

        /**
         * Retourne un bâtisseur dont la frontière en cours de construction est vide.
         */
        public Builder(){
            tuples_build = new long[2];
            size_effective = 0;
        }

        /**
         * Retourne un nouveau bâtisseur avec les
         * mêmes attributs que celui reçu en argument.
         * @param that Bâtisseur à copier.
         **/
        public Builder(Builder that) {
            tuples_build = that.tuples_build.clone();
            size_effective = that.size_effective;
        }

        /**
         * Retourne vrai si et seulement si la frontière en cours de construction est vide.
         * @return Si le tableau tuple est vide.
         */
        public boolean isEmpty(){
            return size_effective == 0;
        }

        /**
         * Vide la frontière en cours de construction en supprimant tous ses éléments.
         * @return Le builder courant.
         */
        public Builder clear(){
            size_effective = 0;
            return this;
        }

        /**
         * Ajoute à la frontière le tuple de critères empaquetés donné.
         * @param packedTuple Le tuple de critères empaquetés qui doit être ajouté.
         * @return Le builder courant.
         */
        public Builder add(long packedTuple){
            int pos = foundPosition(packedTuple);
            if(pos == -1){
                return this;
            }

            int dest = eliminateDominator(pos, packedTuple);
            if(dest >= tuples_build.length){
                long[] tuples_build2 = new long[dest*3/2 +1];
                arraycopy(tuples_build,0,tuples_build2,0,pos);
                arraycopy(tuples_build,pos,tuples_build2,pos+1, dest-pos);
                tuples_build2[pos] = packedTuple;
                size_effective = dest+1;
                tuples_build = tuples_build2;
            }
            else {
                arraycopy(tuples_build,0,tuples_build,0,pos);
                arraycopy(tuples_build,pos,tuples_build,pos+1, dest-pos);
                tuples_build[pos] = packedTuple;
                size_effective = dest+1;
            }

            return this;
        }

        private int eliminateDominator(int pos, long packedTuple)
        {
            int dest = pos;
            for(int i=pos; i<size_effective; i++) {
                if (dominatesOrIsEqual(packedTuple, tuples_build[i]))continue;
                if (dest != i) tuples_build[dest] = tuples_build[i];
                dest += 1;
            }
            return dest;
        }

        private int foundPosition(long packedTuple)
        {
            int pos = -1;
            int size = 0;
            while(size != size_effective && pos == -1)
            {
                if(tuples_build[size] <= withPayload(packedTuple,MAX_PAYLOAD)){
                    if(dominatesOrIsEqual(tuples_build[size], packedTuple)){
                        return -1;
                    }
                    size++;
                }
                else pos = size;
            }

            if(size == size_effective){
                if(size_effective == tuples_build.length){
                    long[] tuples_build1 = new long[size_effective*3/2 +1];
                    arraycopy(tuples_build,0,tuples_build1,0,size_effective);
                    tuples_build = tuples_build1;
                }
                tuples_build[size_effective] = packedTuple;
                size_effective++;
                return -1;
            }

            return pos;
        }

        /**
         * Ajoute à la frontière un tuple de critères augmentés ayant
         * l'heure d'arrivée, le nombre de changements et la charge utile donnés.
         * @param arrMins L'heure d'arrivée donnée.
         * @param changes Le nombre de changements donné.
         * @param payload La charge utile donnée.
         * @return Le builder courant.
         */
        public Builder add(int arrMins, int changes, int payload){
            return add(pack(arrMins,changes,payload));
        }

        /**
         * Ajoute à la frontière tous les tuples présents dans
         * la frontière en cours de construction par le bâtisseur donné.
         * @param that Le bâtisseur donné.
         * @return Le builder courant.
         */
        public Builder addAll(Builder that){
            for(int i=0; i<that.size_effective; i++){
                add(that.tuples_build[i]);
            }
            return this;
        }

        /**
         *  Retourne vrai si et seulement si la totalité des tuples de la frontière donnée,
         *  une fois que leur heure de départ a été fixée sur celle donnée, sont dominés par au moins un tuple du récepteur.
         * @param that La frontière donnée.
         * @param depMins l'heure de départ donnée.
         * @return vrai si et seulement si la totalité des tuples, de la frontière
         * donnée, sont dominés par au moins un tuple du récepteur.
         */
        public boolean fullyDominates(Builder that, int depMins){
            boolean dominate;
            long deps;
           for(int i=0; i<that.size_effective;i++){
               dominate = true;

               deps = withDepMins(that.tuples_build[i], depMins);
               for(int j=0; j<this.size_effective;j++)
               {
                   if(dominatesOrIsEqual(this.tuples_build[j],deps))dominate = false;
               }
               if(dominate) return false;

           }
           return true;
        }

        /**
         * Appelle la méthode accept de l'action de type LongConsumer donnée
         * avec chacun des critères de la frontière, dans l'ordre.
         * @param action L'action a effectué pour chaque critère.
         */
        public void forEach(LongConsumer action){
            for(int i=0; i<size_effective; i++){
                action.accept(tuples_build[i]);
            }
        }

        /**
         * Retourne la frontière de Pareto en cours de construction par ce bâtisseur.
         * @return la frontière de Pareto en cours de construction par ce bâtisseur.
         */
        public ParetoFront build(){
            long[] tuples_build1 = new long[size_effective];
            arraycopy(tuples_build,0,tuples_build1,0,size_effective);
            return new ParetoFront(tuples_build1);
        }
    }
}
