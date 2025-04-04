package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe représente un profil.
 * @param timeTable L'horaire auquel correspond le profil.
 * @param date La date à laquelle correspond le profil.
 * @param arrStationId L'index de la gare d'arrivée à laquelle correspond le profil.
 * @param stationFront La table des frontières de Pareto de toutes les gares.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public record Profile (TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {

    /**
     * Copie la table des frontières de Pareto afin de garantir l'immuabilité de la classe.
     * @param timeTable L'horaire auquel correspond le profil.
     * @param date La date à laquelle correspond le profil.
     * @param arrStationId L'index de la gare d'arrivée à laquelle correspond le profil.
     * @param stationFront La table des frontières de Pareto de toutes les gares.
     */
    public Profile {
        stationFront = List.copyOf(stationFront);
    }

    /**
     * Retourne les liaisons correspondant au profil.
     * @return les liaisons correspondant à la date à laquelle correspond le profil.
     */
    public Connections connections(){
        return timeTable.connectionsFor(date);
    }

    /**
     * Retourne les courses correspondant au profil.
     * @return les courses correspondant à la date à laquelle correspond le profil.
     */
    public Trips trips(){
        return timeTable.tripsFor(date);
    }

    /**
     *  Retourne la frontière de Pareto pour la gare d'index donné.
     * @param stationId L'index de gare dont on désire la frontière pareto.
     * @return la frontière de Pareto pour la gare d'index donné.
     * @throws IndexOutOfBoundsException Si l'index est invalide.
     */
    public ParetoFront forStation(int stationId){
        return stationFront.get(stationId);
    }

    /**
     * La classe représente un bâtisseur de profil.
     */
    public final static class Builder{
        private final TimeTable timeTable;
        private final LocalDate date;
        private final int arrStationId;
        private final ParetoFront.Builder[] stationFrontiers;
        private final ParetoFront.Builder[] tripFrontiers;

        /**
         * Construit un bâtisseur de profil pour l'horaire,
         * la date et la gare de destination donnés.
         * @param timeTable L'horaire donné.
         * @param date La date donnée.
         * @param arrStationId La gare de destination donnée.
         */
        public Builder(TimeTable timeTable, LocalDate date, int arrStationId){
            this.timeTable = timeTable;
            this.date = date;
            this.arrStationId = arrStationId;

            int numberOfStations = timeTable.stations().size();
            stationFrontiers = new ParetoFront.Builder[numberOfStations];

            int numberOfTrips = timeTable.tripsFor(date).size();
            tripFrontiers = new ParetoFront.Builder[numberOfTrips];
        }

        /**
         *  Retourne le bâtisseur de la frontière de Pareto pour la gare d'index donné.
         * @param stationId L'index de gare dont on désire la frontière pareto.
         * @return la frontière de Pareto pour la gare d'index donné.
         * @throws IndexOutOfBoundsException Si l'index est invalide.
         */
        public ParetoFront.Builder forStation(int stationId){
            return stationFrontiers[stationId];
       }

        /**
         * Associe le bâtisseur de frontière de Pareto donné à la gare d'index donné.
         * @param stationId L'index de gare dont on veut associer le bâtisseur de frontière de Pareto.
         * @param builder Le bâtisseur de frontière de Pareto dont on veut associer à la gare.
         * @throws IndexOutOfBoundsException Si l'index est invalide.
         */
       public void setForStation(int stationId, ParetoFront.Builder builder){
            stationFrontiers[stationId] = builder;
       }

        /**
         *  Retourne le bâtisseur de la frontière de Pareto pour la course d'index donné.
         * @param tripId L'index de course dont on désire la frontière pareto.
         * @return la frontière de Pareto pour la course d'index donné.
         * @throws IndexOutOfBoundsException Si l'index est invalide.
         */
       public ParetoFront.Builder forTrip(int tripId){
            return tripFrontiers[tripId];
       }

        /**
         * Associe le bâtisseur de frontière de Pareto donné à la course d'index donné.
         * @param tripId L'index de course dont on veut associer le bâtisseur de frontière de Pareto.
         * @param builder Le bâtisseur de frontière de Pareto dont on veut associer à la course.
         * @throws IndexOutOfBoundsException Si l'index est invalide.
         */
       public void setForTrip(int tripId, ParetoFront.Builder builder){
           tripFrontiers[tripId] = builder;
       }

        /**
         * Retourne le profil simple en cours de construction.
         * @return le profil simple en cours de construction.
         */
       public Profile build(){
            List<ParetoFront> stationFront = new ArrayList<>();
            for(ParetoFront.Builder builder : stationFrontiers){
                if(builder != null) stationFront.add(builder.build());
                else stationFront.add(ParetoFront.EMPTY);
            }
            return new Profile(timeTable,date,arrStationId, stationFront);
        }
    }

}