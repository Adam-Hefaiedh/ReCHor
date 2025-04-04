package ch.epfl.rechor.timetable;

import java.util.NoSuchElementException;

/**
 * L'interface Transfers représente les changements indexés dans un horaire de transport public.
 * Elle permet d'obtenir des informations sur les changements entre gares, comme l'index de la gare de départ,
 * la durée du changement, et les correspondances vers les gares d'arrivée.
 * Les méthodes de cette interface lèvent une IndexOutOfBoundsException en cas d'index invalide.
 */
public interface Transfers extends Indexed {

    /**
     * Retourne l'index de la gare de départ du changement pour le changement d'index donné.
     *
     * @param id l'index du changement
     * @return l'index de la gare de départ associée à ce changement
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    int depStationId(int id);

    /**
     * Retourne la durée, en minutes, du changement pour le changement d'index donné.
     *
     * @param id l'index du changement
     * @return la durée du changement en minutes
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    int minutes(int id);

    /**
     * Retourne l'intervalle empaqueté, selon la convention utilisée par PackedRange,
     * des index des changements dont la gare d'arrivée correspond à l'index donné.
     *
     * @param stationId l'index de la gare d'arrivée
     * @return l'intervalle empaqueté des index des changements pour cette gare d'arrivée
     * @throws IndexOutOfBoundsException si l'index stationId est invalide
     */
    int arrivingAt(int stationId);

    /**
     * Retourne la durée, en minutes, du changement entre les deux gares identifiées par les index donnés.
     * Si aucun changement n'est possible entre ces deux gares, la méthode lève une NoSuchElementException.
     *
     * @param depStationId l'index de la gare de départ
     * @param arrStationId l'index de la gare d'arrivée
     * @return la durée du changement en minutes entre la gare de départ et la gare d'arrivée
     * @throws IndexOutOfBoundsException si l'un des index est invalide
     * @throws NoSuchElementException si aucun changement n'est possible entre les deux gares
     */
    int minutesBetween(int depStationId, int arrStationId);

}