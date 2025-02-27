package ch.epfl.rechor.journey;

import ch.epfl.rechor.IcalBuilder;

import java.rmi.server.UID;
import java.util.StringJoiner;
import java.util.UUID;

public class JourneyIcalConverter {

    // Constructeur privé pour empêcher l'instanciation de cette classe utilitaire
    private JourneyIcalConverter() {}

    /**
     * Convertit un objet Journey en une chaîne au format iCalendar.
     *
     * @param journey l'objet Journey contenant les informations du voyage
     * @return la chaîne iCalendar générée
     */
    public String toIcalendar(Journey journey) {

        // Création d'un StringJoiner pour assembler la description de l'événement,
        // en séparant chaque étape par un saut de ligne (\n)
        StringJoiner j = new StringJoiner("\n");

        // Parcourt chaque étape (leg) du voyage
        for (Journey.Leg leg : journey.getLegs()) {
            // Utilisation d'un switch pour déterminer le type d'étape
            switch(leg) {
                // Si l'étape est une marche (à pied)
                case Journey.Leg.Foot f ->
                    // Affiche un message indiquant l'étape à pied
                        System.out.println("étape à pied : " + f);
                // Si l'étape est un transport
                case Journey.Leg.Transport t ->
                    // Affiche un message indiquant l'étape en transport
                        System.out.println("étape en transport : " + t);
            }
            // Ici, vous pourriez ajouter chaque description d'étape dans le StringJoiner
            // Par exemple :
            // j.add("Description de l'étape " + leg.toString());
        }
        // Récupère la chaîne complète contenant la description de toutes les étapes
        String description = j.toString();

        // Création d'un IcalBuilder pour construire la chaîne iCalendar
        IcalBuilder builder = new IcalBuilder();

        // Construction du calendrier iCalendar avec les propriétés nécessaires
        builder.begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION, "2.0")         // Définition de la version iCalendar
                .add(IcalBuilder.Name.PRODID, "ReCHor")         // Identification du produit
                .begin(IcalBuilder.Component.VEVENT)            // Début de l'événement
                .add(IcalBuilder.Name.UID, UUID.randomUUID().toString()) // Ajout d'un identifiant unique
                .add(IcalBuilder.Name.DTSTAMP, java.time.LocalDateTime.now()) // Ajout de la date/heure de création
                .add(IcalBuilder.Name.DTSTART, journey.depTime()) // Date/heure de début (départ)
                .add(IcalBuilder.Name.DTEND, journey.arrTime())   // Date/heure de fin (arrivée)
                // Résumé indiquant le point de départ et d'arrivée
                .add(IcalBuilder.Name.SUMMARY, journey.depStop().name() + " → " + journey.arrStop().name())
                // Ajout de la description construite à partir des étapes du voyage
                .add(IcalBuilder.Name.DESCRIPTION, description)
                .end(IcalBuilder.Component.VEVENT)              // Fin de l'événement
                .end(IcalBuilder.Component.VCALENDAR);            // Fin du calendrier

        // Génère la chaîne iCalendar finale
        String icalData = builder.build();

        // Affiche le résultat dans la console pour vérification
        System.out.println(icalData);

        // Retourne la chaîne iCalendar générée
        return icalData;
    }
}