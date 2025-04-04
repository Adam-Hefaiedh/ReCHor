package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Transfers;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * La classe permet d'accéder à une table de changements représentée de manière aplatie.
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public final class BufferedTransfers implements Transfers {
    private final StructuredBuffer structuredBuffer;
    private final static int DEP_STATION_ID = 0;
    private final static int ARR_STATION_ID = 1;
    private final static int TRANSFER_MINUTES = 2;
    private final int[] arrStations;

    /**
     * Chaine de caractere contenant les donnees du tableau.
     * @param buffer Tableau de byte contenant les index/donnees du tableau.
     */
    public BufferedTransfers(ByteBuffer buffer){
        Structure structure = new Structure(field(DEP_STATION_ID, Structure.FieldType.U16),
                field(ARR_STATION_ID, Structure.FieldType.U16),
                field(TRANSFER_MINUTES, Structure.FieldType.U8));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        int capacity = 0;
        for(int i=0; i<size(); i++){
            if(structuredBuffer.getU16(ARR_STATION_ID,i)> capacity){
                capacity = structuredBuffer.getU16(ARR_STATION_ID,i);
            }
        }
        arrStations = new int[capacity+1];
        int FirstStation=0;
        int arrStation_id_init =  0;
        for(int i=0; i<size(); i++){
           if (arrStation_id_init != structuredBuffer.getU16(ARR_STATION_ID,i)) {
                arrStations[arrStation_id_init] = PackedRange.pack(FirstStation,i);
                FirstStation = i;
                arrStation_id_init = structuredBuffer.getU16(ARR_STATION_ID,i);
            }
        }
        arrStations[arrStation_id_init] = PackedRange.pack(FirstStation,size());
    }

    /**
     * Retourne l'index de la gare de départ du changement pour le changement d'index donné.
     *
     * @param id l'index du changement.
     * @return l'index de la gare de départ associée à ce changement.
     * @throws IndexOutOfBoundsException si l'index id est invalide.
     */
    @Override
    public int depStationId(int id) {
        return structuredBuffer.getU16(DEP_STATION_ID,id);
    }

    /**
     * Retourne la durée, en minutes, du changement pour le changement d'index donné.
     *
     * @param id l'index du changement.
     * @return la durée du changement en minutes.
     * @throws IndexOutOfBoundsException si l'index id est invalide.
     */
    @Override
    public int minutes(int id) {
        return structuredBuffer.getU8(TRANSFER_MINUTES,id);
    }

    /**
     * Retourne l'intervalle empaqueté, selon la convention utilisée par PackedRange,
     * des index des changements dont la gare d'arrivée correspond à l'index donné.
     *
     * @param stationId l'index de la gare d'arrivée.
     * @return l'intervalle empaqueté des index des changements pour cette gare d'arrivée.
     * @throws IndexOutOfBoundsException si l'index stationId est invalide.
     */
    @Override
    public int arrivingAt(int stationId) {
        return arrStations[stationId];
    }

    /**
     * Retourne la durée, en minutes, du changement entre les deux gares identifiées par les index donnés.
     *
     * @param depStationId l'index de la gare de départ.
     * @param arrStationId l'index de la gare d'arrivée.
     * @return la durée du changement en minutes entre la gare de départ et la gare d'arrivée.
     * @throws IndexOutOfBoundsException si l'un des index est invalide.
     * @throws NoSuchElementException si aucun changement n'est possible entre les deux gares.
     */
    @Override
    public int minutesBetween(int depStationId, int arrStationId) {
        int interval = arrivingAt(arrStationId);
        for(int i=PackedRange.startInclusive(interval);i<PackedRange.endExclusive(interval);i++){
            if(structuredBuffer.getU16(DEP_STATION_ID,i) == depStationId){
                return structuredBuffer.getU8(TRANSFER_MINUTES,i);}
        }
        throw new NoSuchElementException();
    }

    /**
     * Retourne la taille des donnees lier aux changements.
     * @return la taille des donnees lier aux changements.
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
