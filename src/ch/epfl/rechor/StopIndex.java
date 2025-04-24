package ch.epfl.rechor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopIndex {


    private static final Map<Character,String> charRegularExpressions = Map.of(
            'c', "cç",
            'a', "aàáâä",
            'e', "eéèêë",
            'i', "iíìîï",
            'o', "oóòôö",
            'u', "uúùûü"
    );

    private static record StopNames(String source, String principal) {}
    private static record Scored(String name, int score) {}

    private final List<StopNames> stations;

    public StopIndex(List<String> stopIndex, Map<String, String> aliases){
        List<StopNames> list = new ArrayList<>();

        for (String name : stopIndex) {
            list.add(new StopNames(name, name));
        }

        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            list.add(new StopNames(entry.getKey(), entry.getValue()));
        }
        this.stations = List.copyOf(list);
    }


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


    private Pattern SubQueryToRegularExpression(String sub) {
        boolean hasUpper = !sub.equals(sub.toLowerCase(Locale.ROOT));
        int flags = hasUpper
                ? 0
                : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

        StringBuilder regex = new StringBuilder();
        for (char ch : sub.toCharArray()) {
            char charLowerCase = Character.toLowerCase(ch);
            if (charRegularExpressions.containsKey(charLowerCase)) {
                String variants = charRegularExpressions.get(charLowerCase);
                if (hasUpper) {
                    StringBuilder sb = new StringBuilder("[");
                    for (char v : variants.toCharArray()) {
                        sb.append(v).append(Character.toUpperCase(v));
                    }
                    sb.append("]");
                    regex.append(sb);
                } else {
                    regex.append("[").append(variants).append("]");
                }
            } else {
                regex.append(Pattern.quote(String.valueOf(ch)));
            }
        }
        return Pattern.compile(regex.toString(), flags);
    }

    private int scoreSubQuery(String src, String sub, Pattern pattern) {
        Matcher m = pattern.matcher(src);
        if (!m.find()) return 0;
        int start = m.start(), end = m.end();
        int baseLen = sub.length();
        int nameLen = src.length();
        int baseScore = (baseLen * 100) / nameLen;

        int mult = 1;

        if (start == 0 || !Character.isLetter(src.charAt(start-1))) {
            mult *= 4;
        }
        if (end == nameLen || !Character.isLetter(src.charAt(end))) {
            mult *= 2;
        }
        return baseScore * mult;
    }
}