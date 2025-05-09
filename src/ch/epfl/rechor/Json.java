package ch.epfl.rechor;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * L'interface permet la conversion de différentes données au format JSON.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public sealed interface Json {

    /**
     * Cette classe permet de convertir une liste en tableau JSON.
     * @param elements liste qu'on doit convertir.
     */
    record JArray(List<Json> elements) implements Json {
        @Override
        public String toString() {
            StringJoiner result = new StringJoiner(",","[","]");
            elements.forEach(e -> result.add(e.toString()));
            return result.toString();
        }
    }

    /**
     * Cette classe permet de convertir une table associative en objet JSON.
     * @param elements La table associative qu'on doit convertir
     */
    record JObject(Map<String, Json> elements) implements Json {
        @Override
        public String toString() {
            StringJoiner result = new StringJoiner(",","{","}");
            elements.forEach((k,v) -> result.add("\""+k+"\":"+v.toString()));
            return result.toString();
        }
    }

    /**
     * Cette classe permet de convertir une chaine java en chaine JSON.
     * @param s chaine qu'on doit convertir.
     */
    record JString(String s) implements Json {
        @Override
        public String toString() {
            return "\""+s+"\"";
        }
    }

    /**
     * Cette classe permet de convertir un nombre en nombre JSON.
     * @param d nombre qu'on doit convertir.
     */
    record JNumber(double d) implements Json {
        @Override
        public String toString() {
            return Double.toString(d);
        }
    }
}
