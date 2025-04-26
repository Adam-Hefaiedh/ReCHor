package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JourneyGeoJsonConverter {
    private JourneyGeoJsonConverter(){}

    public static Json toGeoJSON(Journey journey)
    {
        List<Json> ride = new ArrayList<>();
        List<Journey.Leg> steps = journey.legs();
        steps.forEach((l) ->{
            ArrayList<Json> coordinate= new ArrayList<>();
            coordinate.add(new Json.JNumber(l.depStop().longitude()));
            coordinate.add(new Json.JNumber(l.depStop().latitude()));
            ride.add(new Json.JArray(coordinate));
            l.intermediateStops().forEach((i) -> {
                ArrayList<Json> subCoordinate = new ArrayList<>();
                subCoordinate.add(new Json.JNumber(i.stop().longitude()));
                subCoordinate.add(new Json.JNumber(i.stop().latitude()));
                ride.add(new Json.JArray(subCoordinate));
            });
        });
        ArrayList<Json> coordinate= new ArrayList<>();
        coordinate.add(new Json.JNumber(steps.getLast().arrStop().longitude()));
        coordinate.add(new Json.JNumber(steps.getLast().arrStop().latitude()));
        ride.add(new Json.JArray(coordinate));
        Map<String, Json> geoJson = new LinkedHashMap<>();
        geoJson.put("type", new Json.JString("LineString"));
        geoJson.put("coordinates", new Json.JArray(ride));
        return new Json.JObject(geoJson);
    }
}
