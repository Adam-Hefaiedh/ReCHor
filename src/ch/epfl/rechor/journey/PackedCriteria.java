package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import static java.lang.Integer.toUnsignedLong;
/**
 * La classe permet de manipuler des critères d'optimisation augmentés,
 * empaquetés dans des valeurs de type long.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class PackedCriteria {
    private static final int MINIMUM_HOURS = -240;
    private static final int MAXIMUM_HOURS = 2880;
    private static final int MAX_BIT_CHANGE = 32;
    private static final int MAX_BIT_DEPART = 51;
    private static final int MAX_BIT_ARR = 39;
    private static final int MAX_HOURS_POSSIBLE = 4095;
    private static final long NO_DEPART = 0x1FFFFFFFFFFFFL;
    private static final int CHANGES_POSITION = 7;
    private static final int CHANGES_SIZE = 127;
    private static final long ADD_CHANGES = 1L;
    private static final long NO_PAYLOAD = ~(0xFFFFFFFFL);

    private PackedCriteria(){}

    /**
     * Retourne la valeur de type long résultant de l'empaquetage de l'heure d'arrivée,
     * du nombre de changements et de la « charge utile » donnés.
     * @param arrMins L'heure d'arrivée.
     * @param changes Le nombre de changements.
     * @param payload La « charge utile ».
     * @return valeur de type long empaquetant les trois valeurs.
     * @throws IllegalArgumentException si arrMins n'est pas compris entre -240(inclus) et
     * 2880(exclus) ou si changes n'est pas de la bonne taille.
     */
    public static long pack(int arrMins, int changes, int payload){
        Preconditions.checkArgument(arrMins >= MINIMUM_HOURS && arrMins < MAXIMUM_HOURS);
        Preconditions.checkArgument(changes >> CHANGES_POSITION == 0);
        return toUnsignedLong(arrMins- MINIMUM_HOURS) << MAX_BIT_ARR |
                toUnsignedLong(changes) <<  MAX_BIT_CHANGE | toUnsignedLong(payload);
    }

    /**
     * Retourne vrai ssi les critères empaquetés donnés incluent une heure de départ.
     * @param criteria Les critères empaquetés.
     * @return vrai si les critères empaquetés contiennent une heure de départ.
     */
    public static boolean hasDepMins(long criteria)
    {
        return criteria >>>  MAX_BIT_DEPART != 0;
    }

    /**
     * Retourne l'heure de départ (en minutes après minuit)
     * des critères empaquetés donnés.
     * @param criteria Les critères empaquetés.
     * @return l'heure de départ.
     * @throws IllegalArgumentException si la valeur long ne contient pas d'heure de départ.
     */
    public static int depMins(long criteria){
        Preconditions.checkArgument(hasDepMins(criteria));
        return MAX_HOURS_POSSIBLE - (int)(criteria >>>  MAX_BIT_DEPART) + MINIMUM_HOURS;
    }

    /**
     * Retourne l'heure d'arrivée (en minutes après minuit)
     * des critères empaquetés donnés.
     * @param criteria Les critères empaquetés.
     * @return l'heure d'arrivée.
     */
    public static int arrMins(long criteria)
    {

        return (int)(criteria >>> MAX_BIT_ARR & MAX_HOURS_POSSIBLE) + MINIMUM_HOURS;
    }

    /**
     * Retourne le nombre de changements
     * des critères empaquetés donnés.
     * @param criteria Les critères empaquetés.
     * @return le nombre de changements.
     */
    public static int changes(long criteria)
    {
        return (int) (criteria >>  MAX_BIT_CHANGE & CHANGES_SIZE);
    }

    /**
     * Retourne la « charge utile » associée aux critères empaquetés donnés.
     * @param criteria Les critères empaquetés.
     * @return la « charge utile ».
     */
    public static int payload(long criteria){
        return (int) (criteria);
    }

    /**
     * Retourne vrai ssi les premiers critères empaquetés dominent où sont égaux aux seconds.
     * @param criteria1 Les premiers critères empaquetés.
     * @param criteria2 Les seconds critères empaquetés.
     * @return vrai si les premiers critères empaquetés dominent où sont égaux aux seconds.
     * @throws IllegalArgumentException si un critère empaqueté possède une heure de départ alors que l'autre non.
     */
    public static boolean dominatesOrIsEqual(long criteria1, long criteria2){
        Preconditions.checkArgument(hasDepMins(criteria1) == hasDepMins(criteria2));
        if(hasDepMins(criteria1))
        {
           return depMins(criteria1) >= depMins(criteria2) && arrMins(criteria1) <=
                   arrMins(criteria2) && changes(criteria1) <= changes(criteria2);
        }
        else {
            return arrMins(criteria1) <= arrMins(criteria2) && changes(criteria1) <= changes(criteria2);
        }
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés, mais sans heure de départ.
     * @param criteria Les critères empaquetés.
     * @return les nouveaux critères empaquetés sans heure de départ.
     */
    public static long withoutDepMins(long criteria){
        return criteria & NO_DEPART;
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés, mais avec l'heure de départ donnée.
     * @param criteria Les critères empaquetés.
     * @param depMins1 La nouvelle heure de départ.
     * @return les critères empaquetés avec une nouvelle heure de départ.
     * @throws IllegalArgumentException si depMins n'est pas compris entre -240(inclus) et 2880(exclus).
     */
    public static long withDepMins(long criteria, int depMins1)
    {
        Preconditions.checkArgument(depMins1 >= MINIMUM_HOURS && depMins1 < MAXIMUM_HOURS);
        int depMins2 = MAX_HOURS_POSSIBLE - depMins1 + MINIMUM_HOURS;
        return (withoutDepMins(criteria)) | (long)(depMins2) <<  MAX_BIT_DEPART;
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés, mais avec un changement de plus.
     * @param criteria Les critères empaquetés.
     * @return les critères empaquetés avec un changement ajouté.
     */
    public static long withAdditionalChange(long criteria)
    {
        return criteria + (ADD_CHANGES <<  MAX_BIT_CHANGE);
    }

    /**
     * Retourne des critères empaquetés identiques à ceux donnés,
     * mais avec la « charge utile » donnée.
     * @param criteria Les critères empaquetés.
     * @param payload1 La nouvelle « charge utile ».
     * @return les critères empaquetés avec une nouvelle « charge utile ».
     */
    public static long withPayload(long criteria, int payload1){
        return (criteria & NO_PAYLOAD) | toUnsignedLong(payload1);
    }
}
