package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FileTimeTableTest {
    static Path timeTablePath() {
        var timeTablePath = Path.of("timetable");
        var maybeTestDataDir = System.getenv("RECHOR_TEST_DATA_DIR");
        return maybeTestDataDir != null
                ? Path.of(maybeTestDataDir).resolve(timeTablePath)
                : timeTablePath;
    }

    @Test
    void fileTimeTableThrowsIOExceptionForInexistantPath() {
        assertThrows(IOException.class, () -> {
            FileTimeTable.in(Path.of("ungeiW6zoosooriix6Za"));
        });
    }

    @Test
    void fileTimeTableInCorrectlyReadsStringTable() throws IOException {
        var tt = FileTimeTable.in(timeTablePath());
        if (tt instanceof FileTimeTable ftt) {
            var stringTable = ftt.stringTable();
            assertEquals(41_534, stringTable.size());
            assertEquals("", stringTable.getFirst());
            assertEquals("Ütige", stringTable.getLast());
            assertEquals("Mont-Crosin, Le Sergent", stringTable.get(21521));
        } else {
            fail("FileTimeTable returned instance of unexpected class: " + tt.getClass());
        }
    }

    @Test
    void fileTimeTableInCorrectlyReadsConstantData() throws IOException {
        var tt = FileTimeTable.in(timeTablePath());

        assertEquals(33_359, tt.stations().size());
        assertEquals("Bad Schussenried, Bahnhofstr.", tt.stations().name(2025));

        assertEquals(264, tt.stationAliases().size());
        assertEquals("Berne (CH)", tt.stationAliases().alias(25));

        assertEquals(21_316, tt.platforms().size());
        assertEquals("E", tt.platforms().name(2025));

        assertEquals(7_936, tt.routes().size());
        assertEquals("FUN 2533", tt.routes().name(2025));

        assertEquals(40_330, tt.transfers().size());
        assertEquals(1716, tt.transfers().depStationId(2025));
    }

    @Test
    void fileTimeTableConnectionsForWorks() throws IOException {
        var tt = FileTimeTable.in(timeTablePath());

        {
            var date = LocalDate.of(2025, Month.MARCH, 18);
            var connections = tt.connectionsFor(date);
            assertEquals(2_797_216, connections.size());
            assertEquals(1499, connections.depMins(2025));
        }

        {
            var date = LocalDate.of(2025, Month.MARCH, 22);
            var connections = tt.connectionsFor(date);
            assertEquals(2_226_139, connections.size());
            assertEquals(1705, connections.depMins(2025));
        }
    }

    @Test
    void fileTimeTableConnectionsForAndTripsForThrowUncheckedIoException() throws IOException {
        var tt = FileTimeTable.in(timeTablePath());
        var date = LocalDate.of(2000, Month.JANUARY, 1);
        assertThrows(UncheckedIOException.class, () -> {
            tt.connectionsFor(date);
        });
        assertThrows(UncheckedIOException.class, () -> {
            tt.tripsFor(date);
        });
    }

    @Test
    void fileTimeTableTripsForWorks() throws IOException {
        var tt = FileTimeTable.in(timeTablePath());

        {
            var date = LocalDate.of(2025, Month.MARCH, 18);
            var trips = tt.tripsFor(date);
            assertEquals(206_806, trips.size());
            assertEquals("Turgi", trips.destination(2025));
        }

        {
            var date = LocalDate.of(2025, Month.MARCH, 22);
            var trips = tt.tripsFor(date);
            assertEquals(167_408, trips.size());
            assertEquals("Turgi", trips.destination(2025));
        }
    }
}