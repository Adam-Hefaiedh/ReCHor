package ch.epfl.rechor.timetable;

import java.time.LocalDate;

/**
 * La classe représente un horaire dont les données qui dépendent
 * de la date sont stockées dans un cache.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class CachedTimeTable implements TimeTable{

    private LocalDate date = null;
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
        updateIfNecessary(date);
        return cachedTrip;
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        updateIfNecessary(date);
        return cachedConnection;
    }

    private void updateIfNecessary(LocalDate date){
        if (this.date != date){
            this.date = date;
            cachedConnection = underlyingTimeTable.connectionsFor(date);
            cachedTrip = underlyingTimeTable.tripsFor(date);
        }
    }
}
