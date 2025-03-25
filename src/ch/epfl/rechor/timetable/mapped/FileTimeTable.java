package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
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

        List<String> stringTable = copyOf(Files.readAllLines(directory.resolve("strings.txt"), StandardCharsets.ISO_8859_1));
        FileChannel channel = FileChannel.open(directory.resolve("stations.bin"));
        Stations stations = new BufferedStations(stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        channel = FileChannel.open(directory.resolve("station-aliases.bin"));
        StationAliases stationAliases = new BufferedStationAliases(stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        channel = FileChannel.open(directory.resolve("platforms.bin"));
        Platforms platforms = new BufferedPlatforms(stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        channel = FileChannel.open(directory.resolve("routes.bin"));
        Routes routes = new BufferedRoutes(stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        channel = FileChannel.open(directory.resolve("transfers.bin"));
        Transfers transfers = new BufferedTransfers(channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        return new FileTimeTable(directory, stringTable, stations, stationAliases, platforms, routes, transfers);
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
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR)
                .appendLiteral('-')
                .appendValue(ChronoField.MONTH_OF_YEAR,2)
                .appendLiteral('-')
                .appendValue(ChronoField.DAY_OF_MONTH,2)
                .toFormatter();
        try {
            FileChannel channel = FileChannel.open(directory.resolve(fmt.format(date) + "\\trips.bin"));
            BufferedTrips Trips = new BufferedTrips(this.stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
            channel.close();
            return Trips;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        try {
            FileChannel channel = FileChannel.open(directory.resolve(date.toString()).resolve("connections.bin"));
            FileChannel channel1 = FileChannel.open(directory.resolve(date.toString()).resolve( "connections-succ.bin"));
            BufferedConnections connections = new BufferedConnections(channel.map(FileChannel.MapMode.READ_ONLY,
                    0,channel.size()), channel1.map(FileChannel.MapMode.READ_ONLY,0,channel1.size()));
            channel.close();
            channel1.close();
            return connections;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
