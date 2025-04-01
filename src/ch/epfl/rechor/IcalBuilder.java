package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * La classe IcalBuilder permet de construire des fichiers iCalendar
 * en générant les lignes correspondantes aux différents composants et propriétés.
 */
public final class IcalBuilder {

    /**
     * Enumération des composants de l'iCalendar.
     */
    public enum Component {
        VCALENDAR, // Composant calendrier principal
        VEVENT    // Composant événement
    }

    /**
     * Enumération des noms de propriétés utilisés dans l'iCalendar.
     */
    public enum Name {
        BEGIN,       // Début d'un composant
        END,         // Fin d'un composant
        PRODID,      // Identifiant du produit
        VERSION,     // Version de l'iCalendar
        UID,         // Identifiant unique de l'événement
        DTSTAMP,     // Date et heure de création
        DTSTART,     // Date et heure de début de l'événement
        DTEND,       // Date et heure de fin de l'événement
        SUMMARY,     // Résumé ou titre de l'événement
        DESCRIPTION // Description détaillée de l'événement
    }

    // Instance de StringBuilder
    private StringBuilder builder = new StringBuilder();

    // Liste des composants démarrés pour gérer la structure de l'iCalendar
    private final List<Component> startingComponents = new ArrayList<>();


    /**
     * Ajoute à l'événement en cours de construction une ligne formée du nom et de la valeur donnés,
     * en pliant la ligne si nécessaire pour respecter la contrainte de 75 caractères maximum par ligne.
     *
     * @param name  le nom de la propriété
     * @param value la valeur de la propriété
     * @return l'instance courante de IcalBuilder pour permettre l'enchaînement des appels
     */
    public IcalBuilder add(Name name, String value){
        String line = name + ":" + value;
        String foldedLine = foldLine(line);
        builder.append(foldedLine);
        return this ;
    }

    /**
     * Plie une ligne selon la spécification iCalendar : aucune ligne ne doit dépasser 75 caractères.
     * Si la ligne est trop longue, elle est découpée en segments de 75 caractères maximum et chaque segment
     * supplémentaire commence par un espace.
     *
     * @param line la ligne à plier
     * @return la ligne pliée selon le format iCalendar
     */
    private String foldLine(String line) {
        StringBuilder folded = new StringBuilder();
        int maxLength = 75;
        int lineLength = line.length();
        int index = 0;
        int end = Math.min(index + maxLength, lineLength);
        folded.append(line, index, end);
        folded.append("\r\n");
        index = end;
        while (index < lineLength) {
            folded.append(" ");
            end = Math.min(index + maxLength-1, lineLength);
            folded.append(line, index, end);
            folded.append("\r\n");
            index = end;
        }
        return folded.toString();
    }
    /**
     * Ajoute une propriété de type date/heure en formatant la date selon le standard iCalendar.
     *
     * @param name      le nom de la propriété
     * @param dateTime  la date et l'heure à ajouter
     * @return l'instance courante de IcalBuilder pour permettre l'enchaînement des appels
     */
    public IcalBuilder add(Name name, LocalDateTime dateTime){
        String dateTimeStr = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
        add(name, dateTimeStr);
        return this;
    }

    /**
     * Commence un nouveau composant dans l'iCalendar.
     *
     * @param component le composant a démarré (par exemple VCALENDAR ou VEVENT).
     * @return l'instance courante de IcalBuilder pour permettre l'enchaînement des appels
     */
    public IcalBuilder begin(Component component){
        add(Name.BEGIN, component.toString());
        startingComponents.add(component);
        return this;
    }

    /**
     * Termine un composant précédemment commencé dans l'iCalendar.
     *
     * Vérifie que le dernier composant ouvert est bien celui qui doit être fermé,
     * puis le retire de la liste des composants ouverts et ajoute la ligne de fin correspondante.
     *
     * @return l'instance courante de IcalBuilder pour permettre l'enchaînement des appels
     * @throws IllegalArgumentException Si il n'y a pas de composante qui ont commencer
     */
    public IcalBuilder end(){
        Preconditions.checkArgument(!startingComponents.isEmpty());
        Component lastComponentOpened = startingComponents.removeLast();
        add(Name.END,lastComponentOpened.toString());
        return this;
    }

    /**
     * Construit la chaîne de caractères finale représentant le contenu complet de l'iCalendar.
     *
     * Vérifie que tous les composants ouverts ont été correctement fermés.
     * Concatène les lignes générées en les séparant par un retour chariot suivi d'un saut de ligne ('\r\n').
     *
     * @return la chaîne de caractères finale correspondant à l'iCalendar construit
     * @throws IllegalArgumentException s'il reste des composants qui n'ont pas fini
     */
    public String build(){
        Preconditions.checkArgument(startingComponents.isEmpty());
        return builder.toString();
    }
}