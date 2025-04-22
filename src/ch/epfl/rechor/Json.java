package ch.epfl.rechor;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public sealed interface Json {
    public record JArray(List<Json> elements) implements Json {
        @Override
        public String toString() {
            StringJoiner result = new StringJoiner(",","[","]");
            elements.forEach(e -> result.add(e.toString()));
            return result.toString();
        }
    }

    public record JObject(Map<String, Json> elements) implements Json {
        @Override
        public String toString() {
            StringJoiner result = new StringJoiner(",","{","}");
            elements.forEach((k,v) -> result.add("\""+k+"\":"+v.toString()));
            return result.toString();
        }
    }

    public record JString(String s) implements Json {
        @Override
        public String toString() {
            return "\""+s+"\"";
        }
    }

    public record JNumber(double d) implements Json {
        @Override
        public String toString() {
            return Double.toString(d);
        }
    }
}
