package ch.epfl.rechor.timetable;

import javax.lang.model.element.Name;
import java.time.LocalDate;

/**
 * L'interface TimeTable représente un horaire de transport public complet.
 * Elle regroupe l'ensemble des données indexées associées à l'horaire, telles que les gares, alias de gares, voies/quais,
 * lignes de transport, changements, courses et liaisons actives pour une date donnée.
 */
public interface TimeTable {

    /**
     * Retourne les gares indexées de l'horaire.
     *
     * @return l'ensemble des gares
     */
    Stations stations();

    /**
     * Retourne les alias des gares indexées de l'horaire.
     *
     * @return l'ensemble des alias de gares
     */
    StationAliases stationAliases();

    /**
     * Retourne les voies ou quais indexés de l'horaire.
     *
     * @return l'ensemble des voies ou quais
     */
    Platforms platforms();

    /**
     * Retourne les lignes de transport public indexées de l'horaire.
     *
     * @return l'ensemble des lignes
     */
    Routes routes();

    /**
     * Retourne les changements (transferts) indexés de l'horaire.
     *
     * @return l'ensemble des changements
     */
    Transfers transfers();

    /**
     * Retourne les courses (trips) indexées actives pour la date donnée.
     *
     * @param date la date considérée
     * @return l'ensemble des courses actives ce jour-là
     */
    Trips tripsFor(LocalDate date);

    /**
     * Retourne les liaisons indexées actives pour la date donnée.
     *
     * @param date la date considérée
     * @return l'ensemble des liaisons actives ce jour-là
     */
    Connections connectionsFor(LocalDate date);

    /**
     * Retourne vrai si et seulement si l'index d'arrêt donné correspond à une gare.
     * Ici, on considère qu'un arrêt est une gare si son index est strictement inférieur
     * au nombre de gares disponibles dans l'horaire.
     *
     * @param stopId l'index de l'arrêt
     * @return vrai si stopId désigne une gare, faux sinon
     */
    default boolean isStationId(int stopId) {
        // ATTENTION : selon la convention, stopId doit être strictement inférieur à stations().size().
        return stopId < stations().size();
    }

    /**
     * Retourne vrai si et seulement si l'index d'arrêt donné correspond à une voie ou un quai.
     * On déduit qu'un arrêt est une voie/quai si son index est supérieur ou égal au nombre de gares.
     *
     * @param stopId l'index de l'arrêt
     * @return vrai si stopId désigne une voie ou un quai, faux sinon
     */
    default boolean isPlatformId(int stopId) {
        // ATTENTION : selon la convention, stopId est considéré comme voie/quai si stopId > stations().size().
        return stopId >= stations().size();
    }

    /**
     * Retourne l'index de la gare correspondant à l'index d'arrêt donné.
     * Si l'arrêt correspond directement à une gare, on retourne stopId.
     * Sinon, pour un arrêt correspondant à une voie ou un quai, on délègue à l'interface Platforms.
     *
     * @param stopId l'index de l'arrêt
     * @return l'index de la gare associée
     */
    default int stationId(int stopId) {
        return isStationId(stopId) ? stopId : platforms().stationId(stopId - stations().size());
    }

    /**
     * Retourne le nom de la voie ou du quai correspondant à l'index d'arrêt donné.
     * Si l'arrêt est une voie ou un quai, on retourne son nom via l'interface Platforms.
     * Sinon, si l'arrêt correspond à une gare, cette méthode retourne null.
     *
     * @param stopId l'index de l'arrêt
     * @return le nom de la voie ou du quai, ou null si l'arrêt correspond à une gare
     */
    default String platformName(int stopId) {
        return isPlatformId(stopId) ? platforms().name(stopId - stations().size()) : null;
    }
}