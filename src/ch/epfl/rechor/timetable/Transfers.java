package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

public interface Transfers extends Indexed{

    //retourne l'index de la gare de départ du changement d'index donné
    abstract int depStationId(int id);

    //retourne la durée, en minutes, du changement d'index donné
    abstract int minutes(int id);

    //retourne l'intervalle empaqueté selon la convention utilisée par PackedRange
    // des index des changements dont la gare d'arrivée est celle d'index donné
    abstract int arrivingAt(int stationId);

    //retourne la durée, en minutes, du changement entre les deux gares d'index donnés,
    // ou lève NoSuchElementException si aucun changement n'est possible entre ces deux gares.
    abstract int minutesBetween(int depStationId, int arrStationId);

}
