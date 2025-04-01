package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static java.util.List.copyOf;

/**
 * La classe représente un horaire de transport public dont les données (aplaties) sont stockées dans des fichiers.
 * @param directory
 * Le chemin d'accès au dossier contenant les fichiers des données horaires
 * @param stringTable
 * La table des chaînes de caractères
 * @param stations
 * Les gares
 * @param stationAliases
 * Les noms alternatifs des gares
 * @param platforms
 * Les voies/quai
 * @param routes
 * Les lignes
 * @param transfers
 * Les changements
 */
public record FileTimeTable(Path directory, List<String> stringTable, Stations stations, StationAliases stationAliases, Platforms platforms, Routes routes, Transfers transfers) implements TimeTable {

    /**
     * Methode facilitant la création d'une instance à partir du
     * chemin d'accès au dossier contenant les données horaires
     * @param directory
     * Le chemin d'accès au dossier contenant les fichiers des données horaires
     * @return L'instance de la classe FileTimeTable du chemin d'accès au dossier contenant les données horaires
     * @throws IOException
     * Si le dossier est introuvable
     */
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

    /**
     * Retourne les gares indexées de l'horaire.
     *
     * @return l'ensemble des gares
     */
    @Override
    public Stations stations() {
        return stations;
    }

    /**
     * Retourne les alias des gares indexées de l'horaire.
     *
     * @return l'ensemble des alias de gares
     */
    @Override
    public StationAliases stationAliases() {
        return stationAliases;
    }

    /**
     * Retourne les voies ou quais indexés de l'horaire.
     *
     * @return l'ensemble des voies ou quais
     */
    @Override
    public Platforms platforms() {
        return platforms;
    }

    /**
     * Retourne les lignes de transport public indexées de l'horaire.
     *
     * @return l'ensemble des lignes
     */
    @Override
    public Routes routes() {
        return routes;
    }

    /**
     * Retourne les changements (transferts) indexés de l'horaire.
     *
     * @return l'ensemble des changements
     */
    @Override
    public Transfers transfers() {
        return transfers;
    }

    /**
     * Retourne les courses (trips) indexées actives pour la date donnée.
     *
     * @param date la date considérée
     * @return l'ensemble des courses actives ce jour-là
     */
    @Override
    public Trips tripsFor(LocalDate date) {
        Path path = directory.resolve(date.toString());
        try {
            return new BufferedTrips(this.stringTable,
                    extractData("trips.bin",
                            path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Retourne les liaisons indexées actives pour la date donnée.
     *
     * @param date la date considérée
     * @return l'ensemble des liaisons actives ce jour-là
     */
    @Override
    public Connections connectionsFor(LocalDate date) {
        try {
            Path path = directory.resolve(date.toString());
            return new BufferedConnections(extractData("connections.bin",
                    path),
                    extractData("connections-succ.bin",
                            path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
