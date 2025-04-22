package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JourneyGeoJsonConverter {
    private JourneyGeoJsonConverter(){}

    public static void main(String[] args) throws IOException {
        long tStart = System.nanoTime();

        TimeTable timeTable = new CachedTimeTable(FileTimeTable.in(Path.of("timetable")));
        Stations stations = timeTable.stations();
        LocalDate date = LocalDate.of(2025, Month.APRIL, 1);
        int depStationId = 7862;
        int arrStationId = 1492;
        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);
        Journey journey = JourneyExtractor
                .journeys(profile, depStationId)
                .get(14);
        System.out.println(JourneyIcalConverter.toIcalendar(journey));

        System.out.println(JourneyGeoJsonConverter.toGeoJSON(journey));

        double elapsed = (System.nanoTime() - tStart) * 1e-9;
        System.out.printf("Temps écoulé : %.3f s%n", elapsed);
    }

    public static Json toGeoJSON(Journey journey)
    {
        List<Json> ride = new ArrayList<>();
        journey.legs().forEach((l) ->{
            List<Json> coordinate = new ArrayList<>();
            coordinate.add(new Json.JNumber(l.depStop().longitude()));
            coordinate.add(new Json.JNumber(l.depStop().latitude()));
            ride.add(new Json.JArray(coordinate));
            coordinate.clear();
            l.intermediateStops().forEach((i) -> {
                coordinate.add(new Json.JNumber(i.stop().longitude()));
                coordinate.add(new Json.JNumber(i.stop().latitude()));
                ride.add(new Json.JArray(coordinate));
                coordinate.clear();
            });
            coordinate.add(new Json.JNumber(l.arrStop().longitude()));
            coordinate.add(new Json.JNumber(l.arrStop().latitude()));
            ride.add(new Json.JArray(coordinate));
        });
        Map<String, Json> geoJson = Map.of("type", new Json.JString("LineString"),
                "coordinates", new Json.JArray(ride));
        return new Json.JObject(geoJson);
    }
}
