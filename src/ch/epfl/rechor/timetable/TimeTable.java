package ch.epfl.rechor.timetable;

import java.time.LocalDate;

public interface TimeTable {

    abstract Stations stations();
    abstract StationAliases stationAliases();
    abstract Platforms platforms();
    abstract Routes routes();
    abstract Transfers transfers();
    abstract Trips tripsFor(LocalDate date);
    abstract Connections connectionsFor(LocalDate date);

    /**
     * Retourne vrai si et seulement si l'index d'arrêt donné correspond à une gare.
     * Ici, on considère qu'un arrêt est une gare si son index est strictement inférieur
     * au nombre de gares disponibles dans l'horaire.
     *
     * @param stopId l'index de l'arrêt
     * @return vrai si stopId désigne une gare, faux sinon
     */
    default boolean isStationId(int stopId){
        return stopId <= stations().size();
    }

    /**
     * Retourne vrai si et seulement si l'index d'arrêt donné correspond à une voie ou un quai.
     * On déduit qu'un arrêt est une voie/quai si son index est supérieur ou égal au nombre de gares.
     *
     * @param stopId l'index de l'arrêt
     * @return vrai si stopId désigne une voie ou un quai, faux sinon
     */
    default boolean isPlatformId(int stopId){
        return stopId > stations().size();
    }

    default int stationId(int stopId) {
        if (isStationId(stopId) == true) {
            return stopId;
        }
        return platforms().stationId(stopId);
    }
    default String platformName (int stopId){
        if (isPlatformId(stopId) == true) {
            return platforms().name(stopId);
        }
        return null;
    }

}
