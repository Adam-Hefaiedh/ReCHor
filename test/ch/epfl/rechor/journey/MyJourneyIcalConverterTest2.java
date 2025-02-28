package ch.epfl.rechor.journey;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Test class for JourneyIcalConverter
 */
public class MyJourneyIcalConverterTest2 {

    @Test
    public void testBasicJourneyConversion() {
        // Create a simple journey with one leg
        Journey journey = createSimpleJourney();

        // Convert to iCalendar
        String ical = JourneyIcalConverter.toIcalendar(journey);

        // Check for required elements
        assertTrue(ical.contains("BEGIN:VCALENDAR"), "Should contain VCALENDAR begin");
        assertTrue(ical.contains("VERSION:2.0"), "Should contain correct VERSION");
        //assertTrue(ical.contains("PRODID:ReCHor"), "Should contain correct PRODID");
        assertTrue(ical.contains("BEGIN:VEVENT"), "Should contain VEVENT begin");

        // Check for UID (should be a UUID)
        assertTrue(ical.contains("UID:"), "Should contain a UID");
        Pattern uuidPattern = Pattern.compile("UID:([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})");
        Matcher matcher = uuidPattern.matcher(ical);
        assertTrue(matcher.find(), "UID should be a valid UUID");

        assertTrue(ical.contains("DTSTAMP:"), "Should contain a DTSTAMP");
        assertTrue(ical.contains("DTSTART:20250218T161300"), "Should contain correct DTSTART");
        assertTrue(ical.contains("DTEND:20250218T163300"), "Should contain correct DTEND");
        //assertTrue(ical.contains("SUMMARY:Lausanne → Geneva"), "Should contain correct SUMMARY");
        assertTrue(ical.contains("DESCRIPTION:"), "Should contain a DESCRIPTION");
        assertTrue(ical.contains("END:VEVENT"), "Should contain VEVENT end");
        assertTrue(ical.contains("END:VCALENDAR"), "Should contain VCALENDAR end");

        // Check that no line exceeds 75 characters
        String[] lines = ical.split("\r\n");
        for (String line : lines) {
            assertTrue(line.length() <= 75, "Each line should be at most 75 characters: " + line);
        }
    }

    @Test
    public void testComplexJourneyConversion() {
        // Create a complex journey with multiple legs
        Journey journey = createComplexJourney();

        // Convert to iCalendar
        String ical = JourneyIcalConverter.toIcalendar(journey);

        // Check for required elements
        assertTrue(ical.contains("BEGIN:VCALENDAR"), "Should contain VCALENDAR begin");
        assertTrue(ical.contains("VERSION:2.0"), "Should contain correct VERSION");
        assertTrue(ical.contains("PRODID:ReCHor"), "Should contain correct PRODID");
        assertTrue(ical.contains("BEGIN:VEVENT"), "Should contain VEVENT begin");

        // Check for UID (should be a UUID)
        assertTrue(ical.contains("UID:"), "Should contain a UID");
        Pattern uuidPattern = Pattern.compile("UID:([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})");
        Matcher matcher = uuidPattern.matcher(ical);
        assertTrue(matcher.find(), "UID should be a valid UUID");

        assertTrue(ical.contains("DTSTAMP:"), "Should contain a DTSTAMP");
        assertTrue(ical.contains("DTSTART:20250218T161300"), "Should contain correct DTSTART");
        assertTrue(ical.contains("DTEND:20250218T175700"), "Should contain correct DTEND");
        assertTrue(ical.contains("SUMMARY:Ecublens VD, EPFL → Gruyères"), "Should contain correct SUMMARY");

        // Check description contains all legs
        String description = extractDescription(ical);

        // Check first leg (metro)
        assertTrue(description.contains("16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)"),
                "Description should contain first leg (metro)");

        // Check second leg (foot)
        assertTrue(description.contains("trajet à pied (3 min)"),
                "Description should contain second leg (foot)");

        // Check third leg (train)
        assertTrue(description.contains("16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)"),
                "Description should contain third leg (train)");

        // Check fourth leg (foot - change platforms)
        assertTrue(description.contains("changement (5 min)"),
                "Description should contain fourth leg (foot - change platforms)");

        // Check that no line exceeds 75 characters
        String[] lines = ical.split("\r\n");
        for (String line : lines) {
            assertTrue(line.length() <= 75, "Each line should be at most 75 characters: " + line);
        }

        // Check newlines in description
        int newlineCount = description.split("\\\\n").length - 1;
        assertTrue(newlineCount >= 8, "Description should contain at least 8 newlines for the 9 journey legs");
    }

    /**
     * Extract and unfold the DESCRIPTION property from an iCalendar string
     */
    private String extractDescription(String ical) {
        StringBuilder description = new StringBuilder();
        boolean inDescription = false;

        for (String line : ical.split("\r\n")) {
            if (line.startsWith("DESCRIPTION:")) {
                inDescription = true;
                description.append(line.substring("DESCRIPTION:".length()));
            } else if (inDescription && line.startsWith(" ")) {
                description.append(line.substring(1));
            } else if (inDescription) {
                break;
            }
        }

        return description.toString();
    }

    /**
     * Create a simple journey with one transport leg
     */
    private Journey createSimpleJourney() {
        // Create departure and arrival stops
        Stop lausanne = new Stop("Lausanne", null, 6.62909, 46.51679);
        Stop geneva = new Stop("Geneva", null, 6.14569, 46.20439);

        // Create departure and arrival times
        LocalDateTime depTime = LocalDateTime.of(2025, 2, 18, 16, 13, 0);
        LocalDateTime arrTime = LocalDateTime.of(2025, 2, 18, 16, 33, 0);

        // Create a transport leg
        Journey.Leg.Transport transportLeg = new Journey.Leg.Transport(
                lausanne,
                depTime,
                geneva,
                arrTime,
                List.of(),  // No intermediate stops
                Vehicle.TRAIN,
                "IR 90",
                "Geneva"
        );

        // Create the journey with the single leg
        return new Journey(List.of(transportLeg));
    }

    /**
     * Create a complex journey with multiple legs, similar to the example in the documentation
     */
    private Journey createComplexJourney() {
        // Create stops
        Stop epfl = new Stop("Ecublens VD, EPFL", null, 6.56, 46.52);
        Stop renensGare = new Stop("Renens VD, gare", null, 6.58, 46.53);
        Stop renensVoie4 = new Stop("Renens VD", "4", 6.58, 46.53);
        Stop lausanneVoie5 = new Stop("Lausanne", "5", 6.63, 46.52);
        Stop lausanneVoie1 = new Stop("Lausanne", "1", 6.63, 46.52);
        Stop romontVoie2 = new Stop("Romont FR", "2", 6.92, 46.69);
        Stop bulleVoie2 = new Stop("Bulle", "2", 7.06, 46.62);
        Stop bulleVoie4 = new Stop("Bulle", "4", 7.06, 46.62);
        Stop gruyeres = new Stop("Gruyères", null, 7.08, 46.58);

        // Create times
        LocalDateTime t1613 = LocalDateTime.of(2025, 2, 18, 16, 13, 0);
        LocalDateTime t1619 = LocalDateTime.of(2025, 2, 18, 16, 19, 0);
        LocalDateTime t1622 = LocalDateTime.of(2025, 2, 18, 16, 22, 0);
        LocalDateTime t1626 = LocalDateTime.of(2025, 2, 18, 16, 26, 0);
        LocalDateTime t1633 = LocalDateTime.of(2025, 2, 18, 16, 33, 0);
        LocalDateTime t1638 = LocalDateTime.of(2025, 2, 18, 16, 38, 0);
        LocalDateTime t1640 = LocalDateTime.of(2025, 2, 18, 16, 40, 0);
        LocalDateTime t1713 = LocalDateTime.of(2025, 2, 18, 17, 13, 0);
        LocalDateTime t1716 = LocalDateTime.of(2025, 2, 18, 17, 16, 0);
        LocalDateTime t1722 = LocalDateTime.of(2025, 2, 18, 17, 22, 0);
        LocalDateTime t1741 = LocalDateTime.of(2025, 2, 18, 17, 41, 0);
        LocalDateTime t1744 = LocalDateTime.of(2025, 2, 18, 17, 44, 0);
        LocalDateTime t1750 = LocalDateTime.of(2025, 2, 18, 17, 50, 0);
        LocalDateTime t1757 = LocalDateTime.of(2025, 2, 18, 17, 57, 0);

        // Create legs
        List<Journey.Leg> legs = new ArrayList<>();

        // 1. EPFL to Renens by metro
        legs.add(new Journey.Leg.Transport(
                epfl, t1613,
                renensGare, t1619,
                List.of(),
                Vehicle.METRO,
                "m1",
                "Renens VD, gare"
        ));

        // 2. Renens walk to platform 4
        legs.add(new Journey.Leg.Foot(
                renensGare, t1619,
                renensVoie4, t1622
        ));

        // 3. Renens to Lausanne by train
        legs.add(new Journey.Leg.Transport(
                renensVoie4, t1626,
                lausanneVoie5, t1633,
                List.of(),
                Vehicle.TRAIN,
                "R 4",
                "Bex"
        ));

        // 4. Lausanne platform change
        legs.add(new Journey.Leg.Foot(
                lausanneVoie5, t1633,
                lausanneVoie1, t1638
        ));

        // 5. Lausanne to Romont by train
        legs.add(new Journey.Leg.Transport(
                lausanneVoie1, t1640,
                romontVoie2, t1713,
                List.of(),
                Vehicle.TRAIN,
                "IR 15",
                "Luzern"
        ));

        // 6. Romont platform change
        legs.add(new Journey.Leg.Foot(
                romontVoie2, t1713,
                romontVoie2, t1716
        ));

        // 7. Romont to Bulle by train
        legs.add(new Journey.Leg.Transport(
                romontVoie2, t1722,
                bulleVoie2, t1741,
                List.of(),
                Vehicle.TRAIN,
                "S 50",
                "Bulle"
        ));

        // 8. Bulle platform change
        legs.add(new Journey.Leg.Foot(
                bulleVoie2, t1741,
                bulleVoie4, t1744
        ));

        // 9. Bulle to Gruyères by train
        legs.add(new Journey.Leg.Transport(
                bulleVoie4, t1750,
                gruyeres, t1757,
                List.of(),
                Vehicle.TRAIN,
                "S 50",
                "Montbovon"
        ));

        return new Journey(legs);
    }
}
