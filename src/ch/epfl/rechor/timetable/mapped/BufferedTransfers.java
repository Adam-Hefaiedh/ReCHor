package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Transfers;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

public final class BufferedTransfers implements Transfers {
    private final StructuredBuffer structuredBuffer;
    private final static int DEP_STATION_ID = 0;
    private final static int ARR_STATION_ID = 1;
    private final static int TRANSFER_MINUTES = 2;
    private final int[] arrStations;

    public BufferedTransfers(ByteBuffer buffer){
        Structure structure = new Structure(field(DEP_STATION_ID, Structure.FieldType.U16),
                field(ARR_STATION_ID, Structure.FieldType.U16),
                field(TRANSFER_MINUTES, Structure.FieldType.U8));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        int capacity = 0;
        for(int i=0; i<structuredBuffer.size(); i++){
            if(structuredBuffer.getU16(ARR_STATION_ID,i)> capacity){
                capacity = structuredBuffer.getU16(ARR_STATION_ID,i);
            }
        }
        arrStations = new int[capacity+1];
        int FirstStation=0;
        int arrStation_id_init =  0;
        for(int i=0; i<structuredBuffer.size(); i++){
           if (arrStation_id_init != structuredBuffer.getU16(ARR_STATION_ID,i)) {
                arrStations[arrStation_id_init] = PackedRange.pack(FirstStation,i);
                FirstStation = i;
                arrStation_id_init = structuredBuffer.getU16(ARR_STATION_ID,i);
            }
        }
        arrStations[arrStation_id_init] = PackedRange.pack(FirstStation,structuredBuffer.size());
    }
    @Override
    public int depStationId(int id) {
        return structuredBuffer.getU16(DEP_STATION_ID,id);
    }

    @Override
    public int minutes(int id) {
        return structuredBuffer.getU8(TRANSFER_MINUTES,id);
    }

    @Override
    public int arrivingAt(int stationId) {
        return arrStations[stationId];
    }

    @Override
    public int minutesBetween(int depStationId, int arrStationId) {
        int interval = arrivingAt(arrStationId);
        for(int i=PackedRange.startInclusive(interval);i<PackedRange.endExclusive(interval);i++){
            if(structuredBuffer.getU16(DEP_STATION_ID,i) == depStationId)
                return structuredBuffer.getU8(TRANSFER_MINUTES,i);
        }
        throw new NoSuchElementException();
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
