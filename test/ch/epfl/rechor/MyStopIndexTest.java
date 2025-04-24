package ch.epfl.rechor;
import java.util.*;

public class MyStopIndexTest {

        public static void main(String[] args) {

            List<String> stops = List.of(
                    "Lausanne",
                    "Palézieux",
                    "Mézières FR, village",
                    "Charleville-Mézières"
            );
            Map<String,String> aliases = Map.of(
                    "Losanna", "Lausanne",
                    "Mezieres", "Mézières FR, village"
            );

            StopIndex index = new StopIndex(stops, aliases);


            runTest(index, "lausanne", 5,
                    List.of("Lausanne")
            );

            runTest(index, "losanna", 5,
                    List.of("Lausanne")
            );

            runTest(index, "mez vil", 5,
                    List.of("Mézières FR, village", "Charleville-Mézières")
            );

            runTest(index, "pal", 5,
                    List.of("Palézieux")
            );

            runTest(index, "méz", 5,
                    List.of("Mézières FR, village", "Charleville-Mézières")
            );

            runTest(index, "MARQUEINEXISTANTE", 5,
                    List.of()
            );
        }

        private static void runTest(StopIndex idx, String query, int limit, List<String> expected) {
            List<String> result = idx.stopsMatching(query, limit);
            System.out.printf("Requête %-15s → %s\n", "\"" + query + "\"", result);
            if (!result.equals(expected)) {
                System.err.printf("  !!! ÉCHEC : attendu %s\n", expected);
            }
        }
    }

