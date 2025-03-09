package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

public final class BufferedStations implements Stations {

    public BufferedStations(List<String> stringTable, ByteBuffer buffer){
    }

    @Override
    public String name(int id) {
        return "";
    }

    @Override
    public double longitude(int id) {
        return 0;
    }

    @Override
    public double latitude(int id) {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }
}
