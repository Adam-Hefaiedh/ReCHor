package ch.epfl.rechor.timetable;

/**
 * L'interface Connections représente des liaisons indexées dans un horaire de transport public.
 * Pour l'algorithme de recherche de voyages, les liaisons doivent être ordonnées par heure de départ décroissante.
 */
public interface Connections extends Indexed {

    /**
     * Retourne l'index de l'arrêt de départ de la liaison d'index donné.
     *
     * @param id l'index de la liaison
     * @return l'index de l'arrêt de départ
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    int depStopId(int id);

    /**
     * Retourne l'heure de départ de la liaison d'index donné, exprimée en minutes après minuit.
     *
     * @param id l'index de la liaison
     * @return l'heure de départ en minutes après minuit
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    int depMins(int id);

    /**
     * Retourne l'index de l'arrêt d'arrivée de la liaison d'index donné.
     *
     * @param id l'index de la liaison
     * @return l'index de l'arrêt d'arrivée
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    int arrStopId(int id);

    /**
     * Retourne l'heure d'arrivée de la liaison d'index donné, exprimée en minutes après minuit.
     *
     * @param id l'index de la liaison
     * @return l'heure d'arrivée en minutes après minuit
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    int arrMins(int id);

    /**
     * Retourne l'index de la course à laquelle appartient la liaison d'index donné.
     *
     * @param id l'index de la liaison
     * @return l'index de la course associée
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    int tripId(int id);

    /**
     * Retourne la position de la liaison d'index donné dans la course à laquelle elle appartient.
     * La première liaison d'une course a l'index 0.
     *
     * @param id l'index de la liaison
     * @return la position de la liaison dans la course
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    int tripPos(int id);

    /**
     * Retourne l'index de la liaison suivant celle d'index donné dans la course à laquelle elle appartient.
     * Si la liaison d'index donné est la dernière de la course, cette méthode retourne l'index de la première liaison de la course.
     *
     * @param id l'index de la liaison
     * @return l'index de la liaison suivante ou, si la liaison est la dernière, l'index de la première liaison de la course
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    int nextConnectionId(int id);
}