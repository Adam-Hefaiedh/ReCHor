package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Class contenant les méthodes statiques permettant d'obtenir
 * la représentation textuelle de différents types de données.
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public final class FormatterFr {
    /**
     * Retourne la représentation textuelle de la durée donnée.
     * @param duration durée a representer.
     * @return la représentation textuelle de la durée donnée.
     */
    public static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return (hours > 0 ? hours + " h " : "") + minutes + " min";
    }

    /**
     * Retourne la représentation textuelle de l'heure de la date/heure donnée.
     * @param dateTime date/heure à representer.
     * @return la représentation textuelle de l'heure de la date/heure donnée.
     */
    public static String formatTime(LocalDateTime dateTime) {
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral('h')
                .appendValue(ChronoField.MINUTE_OF_HOUR,2)
                .toFormatter();
        return fmt.format(dateTime);
    }
    /**
     * Retourne la représentation textuelle de la voie ou quai de l'arrêt
     * ou retourne une liste vide s'il n'y a pas de nom de platform.
     * @param stop arret contenant le nom de la plateforme a representer.
     * @return la représentation textuelle de la platform.
     */
    public static String formatPlatformName(Stop stop) {
        if(stop.platformName() == null || stop.platformName().isEmpty()) {
            return "";
        }
        return stop.platformName().matches("[A-Z]+")
                ? "quai " + stop.platformName()
                : "voie "+ stop.platformName();
    }
    /**
     * Retourne la représentation textuelle de l'étape à pied donnée.
     * @param footLeg etape a pied a representer,
     * @return la représentation textuelle de l'étape à pied donnée.
     */
    public static String formatLeg(Journey.Leg.Foot footLeg) {
        return (footLeg.isTransfer() ? "changement " : "trajet à pied ") +
                "(" + formatDuration(footLeg.duration()) + ")";
    }
    /**
     * Retourne la représentation textuelle de l'étape en transport donnée.
     * @param leg l'étape en transport a representer.
     * @return la représentation textuelle de l'étape en transport donnée.
     */
    public static String formatLeg(Journey.Leg.Transport leg) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatTime(leg.depTime()));
        sb.append(" ").append(leg.depStop().name());
        String platformName = formatPlatformName(leg.depStop());
        sb.append(!platformName.isEmpty() ? " (" + platformName + ")" : "");
        sb.append(" → ");
        sb.append(leg.arrStop().name()).append(" ");
        sb.append("(arr. ").append(formatTime(leg.arrTime()));
        platformName = formatPlatformName(leg.arrStop());
        sb.append(!platformName.isEmpty() ? " " + platformName + ")" :")");
        return String.valueOf(sb);
    }
    /**
     * Retourne la représentation textuelle de la ligne et du sens
     * de parcours du véhicule emprunté pour effectuer l'étape donnée.
     * @param transportLeg la ligne et le sens de parcours du véhicule emprunté à representer.
     * @return la représentation textuelle de la ligne et le sens de parcours du véhicule emprunté pour l'étape.
     */
    public static String formatRouteDestination(Journey.Leg.Transport transportLeg) {
        return transportLeg.route() + " Direction "+transportLeg.destination();
    }
}
