package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import static java.util.List.copyOf;

public record FileTimeTable(Path directory, List<String> stringTable, Stations stations, StationAliases stationAliases, Platforms platforms, Routes routes, Transfers transfers) implements TimeTable {


    public static TimeTable in(Path directory) throws IOException {

        List<String> stringTable = copyOf(Files.readAllLines(directory.resolve("strings.txt"),
                StandardCharsets.ISO_8859_1));
        Stations stations = new BufferedStations(stringTable,
                extractData("stations.bin",directory));
        StationAliases stationAliases = new BufferedStationAliases(stringTable,
                extractData("station-aliases.bin",directory));
        Platforms platforms = new BufferedPlatforms(stringTable,
                extractData("platforms.bin",directory));
        Routes routes = new BufferedRoutes(stringTable,
                extractData("routes.bin",directory));
        Transfers transfers = new BufferedTransfers(
                extractData("transfers.bin",directory));
        return new FileTimeTable(directory, stringTable, stations, stationAliases, platforms, routes, transfers);
    }

    private static ByteBuffer extractData(String docName, Path directory) throws IOException {
        FileChannel channel = FileChannel.open(directory.resolve(docName));
        ByteBuffer data =  channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size());
        channel.close();
        return data;
    }

    @Override
    public Stations stations() {
        return stations;
    }

    @Override
    public StationAliases stationAliases() {
        return stationAliases;
    }

    @Override
    public Platforms platforms() {
        return platforms;
    }

    @Override
    public Routes routes() {
        return routes;
    }

    @Override
    public Transfers transfers() {
        return transfers;
    }

    @Override
    public Trips tripsFor(LocalDate date) {

        try {
            return new BufferedTrips(this.stringTable,
                    extractData((date.toString() + "\\trips.bin"),
                            directory));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        try {
            return new BufferedConnections(extractData(date.toString() + "\\connections.bin",
                    directory),
                    extractData(date.toString() + "\\connections-succ.bin",
                            directory));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
