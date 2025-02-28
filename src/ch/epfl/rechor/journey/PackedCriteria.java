package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;
import static java.lang.Integer.toUnsignedLong;
/**
 * La classe permet de manipuler des critères d'optimisation augmentés,
 * empaquetés dans des valeurs de type long.
 */
public class PackedCriteria {

    private PackedCriteria(){}

    /**
     * retourne la valeur de type long résultant de l'empaquetage de l'heure d'arrivée,
     * du nombre de changements et de la «charge utile» donnés
     * @param arrMins
     * L'heure d'arrivée
     * @param changes
     * Le nombre de changements
     * @param payload
     * La «charge utile»
     * @return valeur de type long empaquetant les 3 valeurs
     */
    public static long pack(int arrMins, int changes, int payload){
        Preconditions.checkArgument(arrMins >= -240 && arrMins < 2880);
        Preconditions.checkArgument(changes >> 7 == 0);
        return toUnsignedLong(arrMins+240) << 39 | toUnsignedLong(changes) << 32 | toUnsignedLong(payload);
    }

    /**
     * Retourne vrai ssi les critères empaquetés donnés incluent une heure de départ
     * @param criteria
     * Les critères empaquetés
     * @return vrai si les critères empaquetés contiennent une heure de départ
     */
    public static boolean hasDepMins(long criteria)
    {
        return criteria >>> 51 != 0;
    }

    /**
     * Retourne l'heure de départ (en minutes après minuit)
     * des critères empaquetés donnés
     * @param criteria
     * Les critères empaqueté
     * @return l'heure de départ
     */
    public static int depMins(long criteria){
        Preconditions.checkArgument(hasDepMins(criteria));
        return 4095 - (int)(criteria >>> 51) -240;
    }

    /**
     * Retourne l'heure d'arrivée (en minutes après minuit)
     * des critères empaquetés donnés
     * @param criteria
     * Les critères empaquetés
     * @return l'heure d'arrivée
     */
    public static int arrMins(long criteria)
    {

        return (int)(criteria >>> 39 & 4095) -240;
    }

    /*
     * Retourne le nombre de changements
     * des critères empaquetés donnés
     * @param criteria
     * Les critères empaquetés
     * @return le nombre de changements
     */
    public static int changes(long criteria)
    {
        return (int) (criteria >> 32 & 127);
    }

    /**
     * retourne la «charge utile»
     * associée aux critères empaquetés donnés
     * @param criteria
     * Les critères empaquetés
     * @return la «charge utile»
     */
    public static int payload(long criteria){
        return (int) (criteria);
    }

    /**
     *  retourne vrai ssi les premiers critères empaquetés
     *  dominent ou sont égaux aux seconds
     * @param criteria1
     * Les premiers critères empaquetés
     * @param criteria2
     * Les seconds critères empaquetés
     * @return vrai si les premiers critères empaquetés dominent ou sont égaux aux seconds
     */
    public static boolean dominatesOrIsEqual(long criteria1, long criteria2){
        Preconditions.checkArgument(hasDepMins(criteria1) == hasDepMins(criteria2));
        if(hasDepMins(criteria1))
        {
           return depMins(criteria1) >= depMins(criteria2) && arrMins(criteria1) <= arrMins(criteria2) && changes(criteria1) <= changes(criteria2);
        }
        else {
            return arrMins(criteria1) <= arrMins(criteria2) && changes(criteria1) <= changes(criteria2);
        }
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés,
     * mais sans heure de départ
     * @param criteria
     * Les critères empaquetés
     * @return les nouveaux critères empaquetés sans heure de départ
     */
    public static long withoutDepMins(long criteria){
        return criteria & 0x1FFFFFFFFFFFFL;
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés,
     * mais avec l'heure de départ donnée
     * @param criteria
     * Les critères empaquetés
     * @param depMins1
     * La nouvelle heure de départ
     * @return les critères empaquetés avec une nouvelle heure de départ
     */
    public static long withDepMins(long criteria, int depMins1)
    {
        Preconditions.checkArgument(depMins1 >= -240 && depMins1 < 2880);
        int depMins2 = 4095 - depMins1 -240;
        return (withoutDepMins(criteria)) | (long)(depMins2) << 51;
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés,
     * mais avec un changement de plus
     * @param criteria
     * Les critères empaquetés
     * @return les critères empaquetés avec un changement ajouter
     */
    public static long withAdditionalChange(long criteria)
    {
        return criteria + (1L << 32);
    }

    /**
     * retourne des critères empaquetés identiques à ceux donnés,
     * mais avec la «charge utile» donnée.
     * @param criteria
     * Les critères empaquetés
     * @param payload1
     * La nouvelle «charge utile»
     * @return les critères empaquetés avec une nouvelle «charge utile»
     */
    public static long withPayload(long criteria, int payload1){
        return (criteria & ~(0xFFFFFFFFL)) | toUnsignedLong(payload1);
    }
}
