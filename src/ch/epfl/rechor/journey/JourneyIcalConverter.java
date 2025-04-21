package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * Classe permettant de convertir un voyage en un événement.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class JourneyIcalConverter {

    private static final String VERSION = "2.0";
    private static final String PRODID = "ReCHor";


    private JourneyIcalConverter() {}

    /**
     * Convertit un objet Journey en une chaîne au format iCalendar.
     *
     * @param journey l'objet Journey contenant les informations du voyage.
     * @return la chaîne iCalendar générée.
     */
    public static String toIcalendar( Journey journey) {


        StringJoiner j = new StringJoiner("\\n");


        for (Journey.Leg leg : journey.legs()) {
            switch(leg) {
                case  Journey.Leg.Foot f ->
                        j.add(FormatterFr.formatLeg(f));
                case Journey.Leg.Transport t ->
                        j.add(FormatterFr.formatLeg(t));
            }
        }
        String description = j.toString();


        IcalBuilder builder = new IcalBuilder();

        builder.begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION, VERSION)
                .add(IcalBuilder.Name.PRODID, PRODID)
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.UID, UUID.randomUUID().toString())
                .add(IcalBuilder.Name.DTSTAMP, java.time.LocalDateTime.now())
                .add(IcalBuilder.Name.DTSTART, journey.depTime())
                .add(IcalBuilder.Name.DTEND, journey.arrTime())
                .add(IcalBuilder.Name.SUMMARY, journey.depStop().name()
                        + " → " + journey.arrStop().name())
                .add(IcalBuilder.Name.DESCRIPTION, description)
                .end()
                .end();


        return builder.build();
    }
}