package ch.epfl.rechor;


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;

public class MyIcalBuilderTest {

    @Test
    void testSimpleIcalEvent() {
        IcalBuilder builder = new IcalBuilder();
        builder.begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION, "2.0")
                .add(IcalBuilder.Name.PRODID, "ReCHor")
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.UID, "123456789")
                .add(IcalBuilder.Name.SUMMARY, "Test Event")
                .add(IcalBuilder.Name.DTSTART, LocalDateTime.of(2025, 2, 18, 16, 30))
                .add(IcalBuilder.Name.DTEND, LocalDateTime.of(2025, 2, 18, 17, 30))
                .end()
                .end();
        String icalString = builder.build();

        assertTrue(icalString.contains("BEGIN:VCALENDAR"));
        assertTrue(icalString.contains("VERSION:2.0"));
        assertTrue(icalString.contains("SUMMARY:Test Event"));
        assertTrue(icalString.contains("END:VCALENDAR"));
    }



    @Test
    void testEndWithoutBegin() {
        IcalBuilder builder = new IcalBuilder();
        assertThrows(IllegalArgumentException.class, builder::end);
    }

    @Test
    void testLineFolding() {
        IcalBuilder builderLong = new IcalBuilder();
        IcalBuilder builderShort = new IcalBuilder();
        String longDescription = "Ceci est un très long résumé qui dépasse les soixante-quinze caractères et doit être plié correctement dans le format iCalendar.Je rajoute des caractères pour test";
        String shortDescription = "Ceci est un court résumé";
        builderLong.begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION, "2.0")
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.DESCRIPTION, longDescription)
                .end()
                .end();
        builderShort.begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION, "2.0")
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.DESCRIPTION, shortDescription)
                .end()
                .end();
        String icalString1 = builderLong.build();
        String icalString2 = builderShort.build();

        // Vérifie que le pliage des lignes respecte la norme iCalendar
        assertTrue(icalString1.contains("DESCRIPTION:Ceci est un très long résumé qui dépasse les soixante-quinze ca\r\n"));
        assertTrue(icalString1.contains(" ractères et doit être plié correctement dans le format iCalendar.Je rajout\r\n"));
        assertTrue(icalString1.contains(" e des caractères pour test\r\n"));

        assertTrue(icalString2.contains("Ceci est un court résumé\r\n"));
    }


    @Test
    void testDateFormat() {
        IcalBuilder builder = new IcalBuilder();
        LocalDateTime date = LocalDateTime.of(2025, 2, 18, 16, 13);
        builder.begin(IcalBuilder.Component.VCALENDAR)
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.DTSTART, date)
                .end()
                .end();
        String icalString = builder.build();

        assertTrue(icalString.contains("DTSTART:20250218T161300"));
    }

    @Test
    void testComponentClosure() {
        IcalBuilder builder = new IcalBuilder();
        builder.begin(IcalBuilder.Component.VCALENDAR)
                .begin(IcalBuilder.Component.VEVENT)
                .end()
                .end();

        String icalString = builder.build();

        assertTrue(icalString.contains("BEGIN:VCALENDAR"));
        assertTrue(icalString.contains("BEGIN:VEVENT"));
        assertTrue(icalString.contains("END:VEVENT"));
        assertTrue(icalString.contains("END:VCALENDAR"));
    }

}