package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class JourneyExtractorTest {
    static Path testPath(String path) {
        var maybeTestDataDir = System.getenv("RECHOR_TEST_DATA_DIR");
        return maybeTestDataDir != null
                ? Path.of(maybeTestDataDir).resolve(path)
                : Path.of(path);
    }

    private static Profile readProfile() throws IOException {
        var timeTable = FileTimeTable.in(testPath("timetable"));
        var date = LocalDate.of(2025, Month.MARCH, 18);
        var arrStationId = stationId(timeTable, "Gruyères");

        var path = testPath("profile_" + date + "_" + arrStationId + ".txt");
        try (BufferedReader r = Files.newBufferedReader(path)) {
            var profileB = new Profile.Builder(timeTable, date, arrStationId);
            var stationId = -1;
            var line = (String) null;
            while ((line = r.readLine()) != null) {
                stationId += 1;
                if (line.isEmpty()) continue;
                var frontB = new ParetoFront.Builder();
                for (var t : line.split(","))
                    frontB.add(Long.parseLong(t, 16));
                profileB.setForStation(stationId, frontB);
            }
            return profileB.build();
        }
    }

    private static Profile readProfileUnchecked() {
        try {
            return readProfile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static int stationId(TimeTable timeTable, String name) {
        var stations = timeTable.stations();
        for (var i = 0; i < stations.size(); i += 1)
            if (stations.name(i).equals(name)) return i;
        throw new NoSuchElementException();
    }

    private static final Profile PROFILE = readProfileUnchecked();

    @Test
    void journeyExtractorJourneysWorksWithSingleTransportLeg() {
        var timeTable = PROFILE.timeTable();
        var depStationId = stationId(timeTable, "Le Pâquier-Montbarry");
        var journeys = JourneyExtractor.journeys(PROFILE, depStationId);
        assertEquals(39, journeys.size());
        for (var journey : journeys) {
            assertEquals(1, journey.legs().size());
            assertInstanceOf(Journey.Leg.Transport.class, journey.legs().getFirst());
        }
    }

    @Test
    void journeyExtractorJourneyWorksWithInitialFootLeg() {
        var timeTable = PROFILE.timeTable();
        var depStationId = stationId(timeTable, "Le Pâquier-Montbarry, gare");
        var journeys = JourneyExtractor.journeys(PROFILE, depStationId);
        assertEquals(39, journeys.size());
        for (var journey : journeys) {
            assertEquals(2, journey.legs().size());
            assertInstanceOf(Journey.Leg.Foot.class, journey.legs().getFirst());
            assertInstanceOf(Journey.Leg.Transport.class, journey.legs().getLast());
        }
    }

    @Test
    void journeyExtractorJourneyWorksWithFinalFootLeg() {
        var timeTable = PROFILE.timeTable();
        var depStationId = stationId(timeTable, "Pringy, village");
        var journeys = JourneyExtractor.journeys(PROFILE, depStationId);
        assertEquals(20, journeys.size());
        for (var journey : journeys) {
            assertEquals(2, journey.legs().size());
            assertInstanceOf(Journey.Leg.Transport.class, journey.legs().getFirst());
            assertInstanceOf(Journey.Leg.Foot.class, journey.legs().getLast());
        }
    }

    @Test
    void journeyExtractorJourneyWorksWithComplexJourney() {
        var timeTable = PROFILE.timeTable();
        var d = PROFILE.date();
        var depStationId = stationId(timeTable, "Martina, cunfin");
        var journeys = JourneyExtractor.journeys(PROFILE, depStationId);
        assertEquals(22, journeys.size());

        var j = journeys.get(6);
        assertEquals(13, j.legs().size());

        if (j.legs().get(4) instanceof Journey.Leg.Transport t) {
            assertEquals("Klosters Platz", t.depStop().name());
            assertEquals("2", t.depStop().platformName());
            assertEquals(46.869210, t.depStop().latitude(), 1e-6);
            assertEquals(9.880940, t.depStop().longitude(), 1e-6);

            assertEquals("Landquart", t.arrStop().name());
            assertEquals("6", t.arrStop().platformName());
            assertEquals(46.967440, t.arrStop().latitude(), 1e-6);
            assertEquals(9.554045, t.arrStop().longitude(), 1e-6);

            var sIt = t.intermediateStops().iterator();
            var s = sIt.next();
            assertEquals(d.atTime(10, 33), s.arrTime());
            assertEquals(d.atTime(10, 33), s.depTime());
            assertEquals("Klosters Dorf", s.stop().name());

            s = sIt.next();
            assertEquals(d.atTime(10, 47), s.arrTime());
            assertEquals(d.atTime(10, 48), s.depTime());
            assertEquals("Küblis", s.stop().name());

            s = sIt.next();
            assertEquals(d.atTime(10, 54), s.arrTime());
            assertEquals(d.atTime(10, 54), s.depTime());
            assertEquals("Jenaz", s.stop().name());

            s = sIt.next();
            assertEquals(d.atTime(10, 59), s.arrTime());
            assertEquals(d.atTime(11, 0), s.depTime());
            assertEquals("Schiers", s.stop().name());

            assertFalse(sIt.hasNext());
        } else fail();

        if (j.legs().get(5) instanceof Journey.Leg.Foot f) {
            assertEquals(d.atTime(11, 10), f.depTime());
            assertEquals(d.atTime(11, 14), f.arrTime());

            assertTrue(f.isTransfer());
        } else fail();
    }
}