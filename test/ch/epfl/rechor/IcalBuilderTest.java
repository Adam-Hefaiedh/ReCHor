package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class IcalBuilderTest {
    private static final String VCALENDAR = "VCALENDAR";
    private static final String VEVENT = "VEVENT";
    private static final String VERSION = "2.0";
    private static final String UID = "B0856A33-2593-4518-A057-3A295F0601F2";
    private static final String DTSTAMP = "20250221T180123";
    private static final String PRODID = "MyProduct";
    private static final String CRLF = "\r\n";

    private static String field(String fieldName, String value) {
        return fieldName + ":" + value + CRLF;
    }

    private static String lines(String... fields) {
        return String.join("", fields);
    }

    @Test
    void icalBuilderBuildWorksForEmptyBuilder() {
        var icalString = new IcalBuilder().build();
        assertEquals("", icalString.trim());
    }

    @Test
    void icalBuilderAddWorksForSingleField() {
        var actual = new IcalBuilder()
                .add(IcalBuilder.Name.VERSION, VERSION)
                .build();
        var expected = field("VERSION", VERSION);
        assertEquals(expected.trim(), actual.trim());
    }

    @Test
    void icalBuilderAddWorksForSeveralFields() {
        var expected = lines(
                field("VERSION", VERSION),
                field("UID", UID),
                field("DTSTAMP", DTSTAMP));
        var actual = new IcalBuilder()
                .add(IcalBuilder.Name.VERSION, VERSION)
                .add(IcalBuilder.Name.UID, UID)
                .add(IcalBuilder.Name.DTSTAMP, DTSTAMP)
                .build();

        assertEquals(expected.trim(), actual.trim());
    }

    @Test
    void icalBuilderAddCorrectlyFormatsDates() {
        var timeStamp = LocalDateTime.of(2025, Month.FEBRUARY, 21, 18, 1, 23);
        var expected = lines(field("DTSTAMP", DTSTAMP));
        var actual = new IcalBuilder()
                .add(IcalBuilder.Name.DTSTAMP, timeStamp)
                .build();
        assertEquals(expected.trim(), actual.trim());
    }

    @Test
    void icalBuilderAddCorrectlyFoldsLines() {
        var description = String.join(" ",
                Collections.nCopies(10, "A very long description that should be folded."));

        var expected = lines(field("DESCRIPTION", description));
        var actual = new IcalBuilder()
                .add(IcalBuilder.Name.DESCRIPTION, description)
                .build();

        for (var line : actual.split(CRLF))
            assertTrue(line.length() <= 75);

        var unfoldedActual = actual.replaceAll(CRLF + " ", "");
        assertEquals(expected.trim(), unfoldedActual.trim());
    }

    @Test
    void icalBuilderBuildThrowsWithUnclosedComponent() {
        assertThrows(RuntimeException.class, () -> {
            new IcalBuilder()
                    .begin(IcalBuilder.Component.VCALENDAR)
                    .build();
        });
    }

    @Test
    void icalBuilderEndCorrectlyClosesComponents() {
        var expected = lines(
                field("BEGIN", VCALENDAR),
                field("PRODID", PRODID),
                field("VERSION", VERSION),
                field("BEGIN", VEVENT),
                field("UID", UID),
                field("DTSTAMP", DTSTAMP),
                field("END", VEVENT),
                field("END", VCALENDAR));

        var actual = new IcalBuilder()
                .begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.PRODID, PRODID)
                .add(IcalBuilder.Name.VERSION, VERSION)
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.UID, UID)
                .add(IcalBuilder.Name.DTSTAMP, DTSTAMP)
                .end()
                .end()
                .build();

        assertEquals(expected.trim(), actual.trim());
    }
}