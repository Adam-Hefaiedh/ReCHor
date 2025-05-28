package ch.epfl.rechor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StopIndex représente un index de noms d’arrêts, avec recherche floue
 * insensible aux accents et à la casse (sous certaines conditions).
 * Il permet de rechercher les arrêts correspondant à une requête textuelle,
 * en les triant par pertinence.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class StopIndex {

    private static final int COEFF_LENGTH      = 100;
    private static final int FIRST_WORD_COEFF  = 4;
    private static final int END_WORD_COEFF    = 2;

    /**
     * Table de correspondance entre une lettre non accentuée et toutes
     * ses variantes accentuées acceptées dans les expressions régulières.
     */
    private static final Map<Character,String> charRegularExpressions = Map.of(
            'c', "cç",
            'a', "aàáâä",
            'e', "eéèêë",
            'i', "iíìîï",
            'o', "oóòôö",
            'u', "uúùûü"
    );

    /**
     * Enregistrement interne représentant un nom d’arrêt
     * - source : nom tel qu’il apparaît dans l’index ou comme alias,
     * - principal : nom officiel de l’arrêt (celui à retourner).
     */
    private record StopNames(String source, String principal) {}

    /**
     * Représente un nom d’arrêt et le score de pertinence associé.
     */
    private record Scored(String name, int score) {}

    private final List<StopNames> stations;

    /**
     * Construit un StopIndex à partir :
     * - d’une liste de noms principaux d’arrêts,
     * - d’une table d’alias associant chaque alias à un nom principal.
     *
     * @param stopIndex liste des noms d’arrêts principaux
     * @param aliases   table alias → nom officiel
     */
    public StopIndex(List<String> stopIndex, Map<String, String> aliases){
        List<StopNames> list = new ArrayList<>();

        for (String name : stopIndex) {
            list.add(new StopNames(name, name));
        }

        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            list.add(new StopNames(entry.getKey(), entry.getValue()));
        }

        this.stations = List.copyOf(list); // rendu immuable
    }

    /**
     * Recherche les noms d’arrêts correspondant à la requête donnée.
     * Chaque sous-requête (mot séparé par un espace) doit apparaître
     * dans le nom source, en respectant les règles d'équivalence.
     *
     * Les résultats sont triés par pertinence décroissante, et les doublons
     * de noms principaux sont éliminés.
     *
     * @param query           requête textuelle
     * @param maxStopNumber   nombre maximum de résultats à retourner
     * @return liste triée de noms d’arrêts pertinents
     */
    public List<String> stopsMatching (String query, int maxStopNumber) {

        Pattern ER = Pattern.compile(" ");
        String[] subQueries = ER.split(query);

        Pattern[] subQueriesWithRE =  Arrays.stream(subQueries)
                .map(this::SubQueryToRegularExpression)
                .toArray(Pattern[]::new);

        List<Scored> scored = new ArrayList<>();
        for (StopNames st : stations) {
            String src = st.source();
            boolean allMatch = true;

            for (Pattern patterns : subQueriesWithRE) {
                if (!patterns.matcher(src).find()) {
                    allMatch = false;
                    break;
                }
            }
            if (!allMatch) continue;

            int score = 0;
            for (int i = 0; i < subQueries.length; i++) {
                score += scoreSubQuery(src, subQueries[i], subQueriesWithRE[i]);
            }

            scored.add(new Scored(st.principal(), score));
        }

        scored.sort(Comparator.comparingInt(Scored::score).reversed());

        List<String> result = new ArrayList<>();
        HashSet<String> seen = new HashSet<>();
        for (Scored s : scored) {
            if (seen.add(s.name())) {
                result.add(s.name());
                if (result.size() == maxStopNumber) break;
            }
        }
        return result;
    }

    /**
     * Transforme une sous-requête utilisateur en une expression régulière,
     * en prenant en compte les équivalences accentuées et la casse :
     * - si la sous-requête contient au moins une majuscule : match exact (sensible à la casse),
     * - sinon : insensible à la casse (flag CASE_INSENSITIVE | UNICODE_CASE).
     *
     * @param sub sous-requête (un mot)
     * @return une Pattern Java correspondante
     */
    private Pattern SubQueryToRegularExpression(String sub) {
        boolean hasUpper = !sub.equals(sub.toLowerCase(Locale.ROOT));
        int flags = hasUpper
                ? 0
                : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

        StringBuilder regex = new StringBuilder();
        for (char ch : sub.toCharArray()) {
            char charLowerCase = Character.toLowerCase(ch);
            if (charRegularExpressions.containsKey(charLowerCase)&& !hasUpper) {
                String variants = charRegularExpressions.get(charLowerCase);
                regex.append("[").append(variants).append("]");
            } else {
                regex.append(Pattern.quote(String.valueOf(ch)));
            }
        }
        return Pattern.compile(regex.toString(), flags);
    }

    /**
     * Calcule un score de pertinence pour une sous-requête dans un nom donné.
     * Le score est :
     * - basé sur la longueur de la sous-requête vs nom,
     * - multiplié par 4 si la sous-requête est au début d’un mot,
     * - multiplié par 2 si elle est à la fin d’un mot.
     *
     * @param src     nom d’arrêt source
     * @param sub     sous-requête textuelle
     * @param pattern regex correspondante
     * @return score de pertinence
     */
    private int scoreSubQuery(String src, String sub, Pattern pattern) {
        Matcher m = pattern.matcher(src);
        if (!m.find()) return 0;
        int start = m.start(), end = m.end();
        int baseLen = sub.length();
        int nameLen = src.length();

        int baseScore = (baseLen * COEFF_LENGTH) / nameLen;

        int mult = 1;

        if (start == 0 || !Character.isLetter(src.charAt(start-1))) {
            mult *= FIRST_WORD_COEFF;
        }

        if (end == nameLen || !Character.isLetter(src.charAt(end))) {
            mult *= END_WORD_COEFF;
        }

        return baseScore * mult;
    }
}