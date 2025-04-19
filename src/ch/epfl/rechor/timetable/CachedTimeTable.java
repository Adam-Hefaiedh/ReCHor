package ch.epfl.rechor.timetable;

import java.time.LocalDate;

public class CachedTimeTable implements TimeTable{

    private LocalDate date;
    private final TimeTable underlyingTimeTable;
    private Connections cachedConnection = null;
    private Trips cachedTrip = null;

    public CachedTimeTable (TimeTable underlyingTimeTable){
        this.underlyingTimeTable = underlyingTimeTable;
    }

    @Override
    public Stations stations() {
        return underlyingTimeTable.stations();
    }

    @Override
    public StationAliases stationAliases() {
        return underlyingTimeTable.stationAliases();
    }

    @Override
    public Platforms platforms() {
        return underlyingTimeTable.platforms();
    }

    @Override
    public Routes routes() {
        return underlyingTimeTable.routes();
    }

    @Override
    public Transfers transfers() {
        return underlyingTimeTable.transfers();
    }

    @Override
    public Trips tripsFor(LocalDate date) {

        if (this.date != date) {
            this.date = date;
            cachedTrip = underlyingTimeTable.tripsFor(date);
        }
        return cachedTrip;
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        if (this.date != date){
            this.date = date;
            cachedConnection = underlyingTimeTable.connectionsFor(date);
        }
        return cachedConnection;
    }
}
