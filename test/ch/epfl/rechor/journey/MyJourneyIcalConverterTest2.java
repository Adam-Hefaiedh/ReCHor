package ch.epfl.rechor.journey;


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyJourneyIcalConverterTest2 {

    @Test
    void testToIcalendarSimpleJourney() {
        Stop start = new Stop("Lausanne", null, 6.62909, 46.51679);
        Stop end = new Stop("Geneve", null, 6.14229, 46.20439);

        Journey.Leg.Transport leg = new Journey.Leg.Transport(
                start, LocalDateTime.of(2025, 2, 18, 16, 13),
                end, LocalDateTime.of(2025, 2, 18, 17, 10),
                List.of(), Vehicle.TRAIN, "IR 15", "Lucerne"
        );

        Journey journey = new Journey(List.of(leg));
        String ical = JourneyIcalConverter.toIcalendar(journey);

        assertTrue(ical.contains("BEGIN:VEVENT"));
        assertTrue(ical.contains("SUMMARY:Lausanne → Geneve"));
        assertTrue(ical.contains("DTSTART:20250218T161300"));
        assertTrue(ical.contains("DTEND:20250218T171000"));
    }

    @Test
    void testToIcalendarComplexJourney() {
        // Création des arrêts
        Stop ecublens = new Stop("Ecublens VD, EPFL", null, 6.566141, 46.522196);
        Stop renens1 = new Stop("Renens VD, gare", null, 6.578519, 46.537619);
        Stop renens2 = new Stop("Renens VD", "4", 6.578519, 46.537619);
        Stop lausanne1 = new Stop("Lausanne", "5", 6.629092, 46.516792);
        Stop lausanne2 = new Stop("Lausanne", "1", 6.629092, 46.516792);
        Stop romont1 = new Stop("Romont FR", "2", 6.911811, 46.693508);
        Stop romont2 = new Stop("Romont FR", "1", 6.911811, 46.693508);
        Stop bulle1 = new Stop("Bulle", "2", 7.056457, 46.624212);
        Stop bulle2 = new Stop("Bulle", "4", 7.056457, 46.624212);
        Stop gruyeres = new Stop("Gruyères", "2", 7.082189, 46.584456);

        // Construction des étapes du voyage
        Journey.Leg.Transport step1 = new Journey.Leg.Transport(
                ecublens,
                LocalDateTime.of(2025, 2, 18, 16, 13),
                renens1,
                LocalDateTime.of(2025, 2, 18, 16, 19),
                List.of(),
                Vehicle.TRAIN, "IC", "Renens VD"
        );

        Journey.Leg.Foot step2 = new Journey.Leg.Foot(
                renens1,
                LocalDateTime.of(2025, 2, 18, 16, 19),
                renens2,
                LocalDateTime.of(2025, 2, 18, 16, 22)
        );

        Journey.Leg.Transport step3 = new Journey.Leg.Transport(
                renens2,
                LocalDateTime.of(2025, 2, 18, 16, 26),
                lausanne1,
                LocalDateTime.of(2025, 2, 18, 16, 33),
                List.of(),
                Vehicle.TRAIN, "IC", "Lausanne"
        );

        Journey.Leg.Foot step4 = new Journey.Leg.Foot(
                lausanne1,
                LocalDateTime.of(2025, 2, 18, 16, 33),
                lausanne2,
                LocalDateTime.of(2025, 2, 18, 16, 38)
        );

        Journey.Leg.Transport step5 = new Journey.Leg.Transport(
                lausanne2,
                LocalDateTime.of(2025, 2, 18, 16, 40),
                romont1,
                LocalDateTime.of(2025, 2, 18, 17, 13),
                List.of(),
                Vehicle.TRAIN, "RE", "Romont FR"
        );

        Journey.Leg.Foot step6 = new Journey.Leg.Foot(
                romont1,
                LocalDateTime.of(2025, 2, 18, 17, 13),
                romont2,
                LocalDateTime.of(2025, 2, 18, 17, 16)
        );

        Journey.Leg.Transport step7 = new Journey.Leg.Transport(
                romont2,
                LocalDateTime.of(2025, 2, 18, 17, 22),
                bulle1,
                LocalDateTime.of(2025, 2, 18, 17, 41),
                List.of(),
                Vehicle.TRAIN, "S20", "Bulle"
        );

        Journey.Leg.Foot step8 = new Journey.Leg.Foot(
                bulle1,
                LocalDateTime.of(2025, 2, 18, 17, 41),
                bulle2,
                LocalDateTime.of(2025, 2, 18, 17, 44)
        );

        Journey.Leg.Transport step9 = new Journey.Leg.Transport(
                bulle2,
                LocalDateTime.of(2025, 2, 18, 17, 50),
                gruyeres,
                LocalDateTime.of(2025, 2, 18, 17, 57),
                List.of(),
                Vehicle.TRAIN, "S21", "Gruyères"
        );

        // Création du voyage complet
        Journey journey = new Journey(List.of(step1, step2, step3, step4, step5, step6, step7, step8, step9));

        // Conversion du voyage en iCalendar
        String icalString = JourneyIcalConverter.toIcalendar(journey);

        assertTrue(icalString.contains("BEGIN:VCALENDAR"));
        assertTrue(icalString.contains("VERSION:2.0"));
        assertTrue(icalString.contains("PRODID:ReCHor"));
        assertTrue(icalString.contains("BEGIN:VEVENT"));
        assertTrue(icalString.contains("SUMMARY:Ecublens VD, EPFL → Gruyères"));
        assertTrue(icalString.contains("DTSTART:20250218T161300"));
        assertTrue(icalString.contains("DTEND:20250218T175700"));


        String expectedDescription = "BEGIN:VCALENDAR\r\n" + //
                "VERSION:2.0\r\n" + //
                "PRODID:ReCHor\r\n" + //
                "BEGIN:VEVENT\r\n" + //
                "UID:bbff399f-d276-4052-8798-9c2f227353de\r\n" + //
                "DTSTAMP:20250216T182750\r\n" + //
                "DTSTART:20250218T161300\r\n" + //
                "DTEND:20250218T175700\r\n" + //
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" + //
                "DESCRIPTION:16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\ntrajet \r\n" + //
                " à pied (3 min)\\n16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\nc\r\n" + //
                " hangement (5 min)\\n16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)\r\n" + //
                " \\nchangement (3 min)\\n17h22 Romont FR (voie 1) → Bulle (arr. 17h41 voie 2)\r\n" + //
                " \\nchangement (3 min)\\n17h50 Bulle (voie 4) → Gruyères (arr. 17h57 voie 2)\r\n" + //
                "END:VEVENT\r\n" + //
                "END:VCALENDAR\r\n";

        // Normalisation et comparaison
        expectedDescription = expectedDescription
                .replaceAll("UID:.*","UID:0")
                .replaceAll("DTSTAMP:.*","00000000T000000");

        icalString = icalString
                .replaceAll("UID:.*","UID:0")
                .replaceAll("DTSTAMP:.*","00000000T000000");

        assertEquals(expectedDescription, icalString, "La description doit contenir les étapes formatées correctement");
    }

    @Test
    void testIcalendarAttributes() {
        Stop start = new Stop("Lausanne", null, 6.62909, 46.51679);
        Stop end = new Stop("Geneve", null, 6.14229, 46.20439);

        Journey.Leg.Transport leg = new Journey.Leg.Transport(
                start, LocalDateTime.of(2025, 2, 18, 16, 13),
                end, LocalDateTime.of(2025, 2, 18, 17, 10),
                List.of(), Vehicle.TRAIN, "IR 15", "Lucerne"
        );

        Journey journey = new Journey(List.of(leg));
        String ical = JourneyIcalConverter.toIcalendar(journey);

        assertTrue(ical.contains("BEGIN:VCALENDAR"), "Doit contenir BEGIN:VCALENDAR");
        assertTrue(ical.contains("END:VCALENDAR"), "Doit contenir END:VCALENDAR");
        assertTrue(ical.contains("BEGIN:VEVENT"), "Doit contenir BEGIN:VEVENT");
        assertTrue(ical.contains("END:VEVENT"), "Doit contenir END:VEVENT");
        assertTrue(ical.contains("VERSION:2.0"), "La version doit être 2.0");
        assertTrue(ical.contains("PRODID:ReCHor"), "Le PRODID doit être ReCHor");
    }
}