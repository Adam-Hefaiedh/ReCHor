package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import static java.util.List.copyOf;

public class FileTimeTable implements TimeTable {

    private Path directory;
    private List<String> stringTable;
    private Stations stations;
    private StationAliases stationAliases;
    private Platforms platforms;
    private Routes routes;
    private Transfers transfers;

    public static TimeTable in(Path directory) throws IOException {
        FileTimeTable fileTimeTable = new FileTimeTable();
        fileTimeTable.directory = directory;
        fileTimeTable.stringTable = copyOf(Files.readAllLines(fileTimeTable.directory.resolve("Strings.txt")));
        FileChannel channel = FileChannel.open(directory.resolve("stations.bin"));
        fileTimeTable.stations = new BufferedStations(fileTimeTable.stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        channel = FileChannel.open(directory.resolve("station-aliases.bin"));
        fileTimeTable.stationAliases = new BufferedStationAliases(fileTimeTable.stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        channel = FileChannel.open(directory.resolve("platforms.bin"));
        fileTimeTable.platforms = new BufferedPlatforms(fileTimeTable.stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        channel = FileChannel.open(directory.resolve("routes.bin"));
        fileTimeTable.routes = new BufferedRoutes(fileTimeTable.stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        channel = FileChannel.open(directory.resolve("transfers.bin"));
        fileTimeTable.transfers = new BufferedTransfers(channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
        channel.close();
        return fileTimeTable;
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
        Path directory = Path.of(fmt.format(date));
        try {
            FileChannel channel = FileChannel.open(directory.resolve("trips.bin"));
            BufferedTrips Trips = new BufferedTrips(this.stringTable, channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()));
            channel.close();
            return Trips;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR)
                .appendLiteral('-')
                .appendValue(ChronoField.MONTH_OF_YEAR,2)
                .appendLiteral('-')
                .appendValue(ChronoField.DAY_OF_MONTH,2)
                .toFormatter();
        Path directory = Path.of(fmt.format(date));
        try {
            FileChannel channel = FileChannel.open(directory.resolve("connections.bin"));
            FileChannel channel1 = FileChannel.open(directory.resolve("connections-succ.bin"));
            BufferedConnections connections = new BufferedConnections(channel.map(FileChannel.MapMode.READ_ONLY,0,channel.size()), channel1.map(FileChannel.MapMode.READ_ONLY,0,channel1.size()));
            channel.close();
            return connections;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
