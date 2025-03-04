package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JourneyIcalConverterTest {
    private static Journey exampleJourney() {
        var s1 = new Stop("Ecublens VD, EPFL", null, 6.566141, 46.522196);
        var s2 = new Stop("Renens VD, gare", null, 6.578519, 46.537619);
        var s3 = new Stop("Renens VD", "4", 6.578935, 46.537042);
        var s4 = new Stop("Lausanne", "5", 6.629092, 46.516792);
        var s5 = new Stop("Lausanne", "1", 6.629092, 46.516792);
        var s6 = new Stop("Romont FR", "2", 6.911811, 46.693508);

        var d = LocalDate.of(2025, Month.FEBRUARY, 18);
        var l1 = new Journey.Leg.Transport(
                s1,
                d.atTime(16, 13),
                s2,
                d.atTime(16, 19),
                List.of(),
                Vehicle.METRO,
                "m1",
                "Renens VD, gare");

        var l2 = new Journey.Leg.Foot(s2, d.atTime(16, 19), s3, d.atTime(16, 22));

        var l3 = new Journey.Leg.Transport(
                s3,
                d.atTime(16, 26),
                s4,
                d.atTime(16, 33),
                List.of(),
                Vehicle.TRAIN,
                "R4",
                "Bex");

        var l4 = new Journey.Leg.Foot(s4, d.atTime(16, 33), s5, d.atTime(16, 38));

        var l5 = new Journey.Leg.Transport(
                s5,
                d.atTime(16, 40),
                s6,
                d.atTime(17, 13),
                List.of(),
                Vehicle.TRAIN,
                "IR15",
                "Luzern");

        return new Journey(List.of(l1, l2, l3, l4, l5));
    }

    @Test
    void journeyIcalConverterToIcalendarWorks() {
        var dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        var vcalendarContext = List.of("VCALENDAR");
        var veventContext = List.of("VCALENDAR", "VEVENT");

        var actual = JourneyIcalConverter.toIcalendar(exampleJourney());
        var unfoldedActual = actual.replaceAll("\r\n ", "");
        var context = new ArrayList<String>();
        var requiredNames = new HashSet<>(
                Set.of("VERSION", "PRODID", "UID", "DTSTAMP", "DTSTART", "SUMMARY", "DESCRIPTION"));

        for (var line : unfoldedActual.split("\r\n")) {
            var colonLoc = line.indexOf(':');
            var name = line.substring(0, colonLoc);
            var value = line.substring(colonLoc + 1);
            requiredNames.remove(name);
            switch (name) {
                case "BEGIN" ->
                        context.addLast(value);
                case "END" -> {
                    assertFalse(context.isEmpty());
                    context.removeLast();
                }
                case "VERSION" -> {
                    assertEquals("2.0", value);
                    assertEquals(vcalendarContext, context);
                }
                case "PRODID" -> {
                    assertEquals("ReCHor", value);
                    assertEquals(vcalendarContext, context);
                }
                case "UID" -> {
                    assertFalse(value.isBlank());
                    assertEquals(veventContext, context);
                }
                case "DTSTAMP" -> {
                    var timeStamp = LocalDateTime.parse(value, dateFmt);
                    var elapsed = Duration.between(timeStamp, LocalDateTime.now()).abs();
                    assertTrue(elapsed.compareTo(Duration.ofSeconds(10)) < 0);
                    assertEquals(veventContext, context);
                }
                case "DTSTART" -> {
                    var timeStart = LocalDateTime.parse(value, dateFmt);
                    assertEquals(exampleJourney().depTime(), timeStart);
                    assertEquals(veventContext, context);
                }
                case "DTEND" -> {
                    var timeEnd = LocalDateTime.parse(value, dateFmt);
                    assertEquals(exampleJourney().arrTime(), timeEnd);
                    assertEquals(veventContext, context);
                }
                case "SUMMARY" -> {
                    assertEquals("Ecublens VD, EPFL → Romont FR", value);
                    assertEquals(veventContext, context);
                }
                case "DESCRIPTION" -> {
                    assertEquals(
                            "16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\n" +
                            "trajet à pied (3 min)\\n" +
                            "16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\n" +
                            "changement (5 min)\\n" +
                            "16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)", value);
                    assertEquals(veventContext, context);
                }
            }
        }
        assertEquals(Set.of(), requiredNames);
    }
}