package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;

import java.util.*;
import java.util.List;

/**
 * La classe permet de convertir des voyages dans la forme GeoJSON.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class JourneyGeoJsonConverter {
    private JourneyGeoJsonConverter(){}

    /**
     * Permet de convertir un voyage en document GeoJSON pour afficher le tracé de ce voyage
     * @param journey Voyage a convertir en GeoJSON
     *
     * @return Le voyage convertit en objet JSON
     */
    public static Json toGeoJSON (Journey journey) {
        List<Json> ride = convertJSON(journey);
        Map<String, Json> geoJson = new LinkedHashMap<>();
        geoJson.put("type", new Json.JString("LineString"));
        geoJson.put("coordinates", new Json.JArray(ride));
        return new Json.JObject(geoJson);
    }

    private static List<Json> convertJSON(Journey journey) {
        List<Journey.Leg> steps = journey.legs();
        List<Json> ride = new ArrayList<>();
        steps.forEach((l) -> {
            ride.add(new Json.JArray(List.of(new Json.JNumber(l.depStop().longitude()),
                    new Json.JNumber(l.depStop().latitude()))));
            l.intermediateStops().forEach((i) ->
                ride.add(new Json.JArray(List.of(new Json.JNumber(i.stop().longitude()),
                        new Json.JNumber(i.stop().latitude()))))
            );
        });
        ride.add(new Json.JArray(List.of(new Json.JNumber(steps.getLast().arrStop().longitude()),
                new Json.JNumber(steps.getLast().arrStop().latitude()))));

        return ride;
    }
}
