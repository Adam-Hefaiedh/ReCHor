package ch.epfl.rechor.journey;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;

/**
 * Tests complets pour JourneyExtractor
 * Cette classe contient des tests exhaustifs pour vérifier toutes les fonctionnalités de JourneyExtractor
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MyjourneyExtractorTest {
    private TimeTable timeTable;
    private JourneyExtractor extractor;
    private static final LocalDate TEST_DATE = LocalDate.of(2025, Month.MARCH, 18);
    private final Map<Integer, String> stationMap = new HashMap<>();

    // Identifiants de stations importantes
    private static final int EPFL_STATION_ID = 7872;            // Ecublens VD, EPFL
    private static final int EPFL_BUS_STATION_ID = 7874;        // Ecublens VD, EPFL (bus)
    private static final int EPFL_PARC_STATION_ID = 8359;       // Ecublens VD, Parc
    private static final int LAUSANNE_STATION_ID = 7880;        // Lausanne
    private static final int GRUYERES_STATION_ID = 11486;       // Gruyères
    private static final int ZURICH_HB_STATION_ID = 8501;       // Zürich HB
    private static final int ZURICH_BAHNHOFPLATZ_ID = 32856;    // Zürich, Bahnhofplatz/HB
    private static final int BERN_STATION_ID = 8507;            // Bern
    private static final int GENEVE_STATION_ID = 8503;          // Genève

    @BeforeAll
    void setUp() throws IOException {
        System.out.println("=== Initialisation des tests ===");
        timeTable = FileTimeTable.in(Path.of("timetable"));;
        System.out.println("=== TimeTable chargé ===");

        // Charger quelques noms de stations pour référence
        loadStationNames();
    }

    private void loadStationNames() {
        stationMap.put(EPFL_STATION_ID, "Ecublens VD, EPFL");
        stationMap.put(EPFL_BUS_STATION_ID, "Ecublens VD, EPFL (bus)");
        stationMap.put(EPFL_PARC_STATION_ID, "Ecublens VD, Parc");
        stationMap.put(LAUSANNE_STATION_ID, "Lausanne");
        stationMap.put(GRUYERES_STATION_ID, "Gruyères");
        stationMap.put(ZURICH_HB_STATION_ID, "Zürich HB");
        stationMap.put(ZURICH_BAHNHOFPLATZ_ID, "Zürich, Bahnhofplatz/HB");
        stationMap.put(BERN_STATION_ID, "Bern");
        stationMap.put(GENEVE_STATION_ID, "Genève");
    }

    @Test
    @DisplayName("Liste des stations disponibles")
    void getStationNames() {
        System.out.println("=== Affichage des 20 premières stations ===");
        for (int i = 0; i < Math.min(20, timeTable.stations().size()); i++) {
            System.out.printf("Index %d, Gare %s%n", i, timeTable.stations().name(i));
        }
        System.out.println("...");
        System.out.println("Nombre total de stations: " + timeTable.stations().size());
    }

    @Test
    @DisplayName("Test de base EPFL → Gruyères")
    void testBasicJourney() throws IOException {
        System.out.println("=== Test du voyage de base EPFL → Gruyères ===");

        // Chargement du profil
        Profile profile = readProfile(timeTable, TEST_DATE, GRUYERES_STATION_ID);

        // Extraction des voyages
        List<Journey> journeys = extractor.journeys(profile, EPFL_STATION_ID);

        // Vérifications
        assertFalse(journeys.isEmpty(), "La liste des voyages ne devrait pas être vide");
        System.out.println("Nombre de voyages trouvés: " + journeys.size());

        // Tester le voyage de référence (32 si disponible)
        int referenceIndex = Math.min(32, journeys.size() - 1);
        Journey referenceJourney = journeys.get(referenceIndex);

        System.out.println("Détail du voyage de référence (#" + referenceIndex + "):");
        printJourneyDetails(referenceJourney);

        // Vérifier la conversion iCalendar
        String ical = JourneyIcalConverter.toIcalendar(referenceJourney);
        System.out.println("Format iCalendar:");
        System.out.println(ical);

        assertTrue(ical.contains("SUMMARY"), "Le format iCalendar devrait contenir un résumé");
    }

    @Test
    @DisplayName("Test avec arrêt de bus EPFL → Gruyères")
    void testBusJourney() throws IOException {
        System.out.println("=== Test du voyage depuis l'arrêt de bus EPFL → Gruyères ===");

        // Chargement du profil
        Profile profile = readProfile(timeTable, TEST_DATE, GRUYERES_STATION_ID);

        // Extraction des voyages

        List<Journey> journeys = extractor.journeys(profile, EPFL_BUS_STATION_ID);

        // Vérifications
        assertFalse(journeys.isEmpty(), "La liste des voyages ne devrait pas être vide");
        System.out.println("Nombre de voyages trouvés: " + journeys.size());

        // Tester un voyage avec une étape à pied initiale
        Journey journey = journeys.get(0);
        System.out.println("Premier voyage:");
        printJourneyDetails(journey);

        // Vérifier qu'il y a bien des étapes dans le voyage
        List<Journey.Leg> legs = journey.legs();
        assertFalse(legs.isEmpty(), "Le voyage devrait avoir des étapes");

        // Vérifier le premier leg pour voir s'il s'agit d'une étape à pied
        if (legs.get(0) instanceof Journey.Leg.Foot) {
            System.out.println("✓ Le voyage commence par une étape à pied comme attendu");
        } else {
            System.out.println("⚠ Le voyage ne commence pas par une étape à pied");
        }

        // Vérification de la structure globale du voyage
        verifyJourneyStructure(journey);
    }

    @Test
    @DisplayName("Test longue distance Zürich → Gruyères")
    void testLongDistanceJourney() throws IOException {
        System.out.println("=== Test du voyage longue distance Zürich → Gruyères ===");

        // Chargement du profil
        Profile profile = readProfile(timeTable, TEST_DATE, GRUYERES_STATION_ID);

        // Extraction des voyages
        List<Journey> journeys = extractor.journeys(profile, ZURICH_BAHNHOFPLATZ_ID);

        // Vérifications
        assertFalse(journeys.isEmpty(), "La liste des voyages ne devrait pas être vide");
        System.out.println("Nombre de voyages trouvés: " + journeys.size());

        // Choisir un voyage à analyser
        int indexToUse = Math.min(journeys.size() - 1, 10);
        Journey journey = journeys.get(indexToUse);

        System.out.println("Analyse du voyage #" + indexToUse);
        printJourneyDetails(journey);

        // Vérifier le nombre d'étapes (un voyage longue distance devrait avoir plusieurs étapes)
        List<Journey.Leg> legs = journey.legs();
        System.out.println("Nombre d'étapes: " + legs.size());

        assertTrue(legs.size() >= 3, "Un voyage longue distance devrait avoir au moins 3 étapes");

        // Compter les changements (nombre d'étapes de transport - 1)
        long transportLegs = legs.stream()
                .filter(leg -> leg instanceof Journey.Leg.Transport)
                .count();

        System.out.println("Nombre de changements: " + (transportLegs - 1));

        // Vérification détaillée de chaque étape
        System.out.println("\nDétail des étapes:");
        for (int i = 0; i < legs.size(); i++) {
            Journey.Leg leg = legs.get(i);
            if (leg instanceof Journey.Leg.Transport) {
                Journey.Leg.Transport transport = (Journey.Leg.Transport) leg;
                System.out.printf("Étape %d: Transport de %s à %s sur %s %s (%d arrêts intermédiaires)%n",
                        i + 1,
                        transport.depStop().name(),
                        transport.arrStop().name(),
                        transport.vehicle(),
                        transport.route(),
                        transport.intermediateStops().size());
            } else if (leg instanceof Journey.Leg.Foot) {
                Journey.Leg.Foot foot = (Journey.Leg.Foot) leg;
                System.out.printf("Étape %d: Marche de %s à %s%n",
                        i + 1,
                        foot.depStop().name(),
                        foot.arrStop().name());
            }
        }

        // Vérifier la cohérence du voyage
        verifyJourneyStructure(journey);
    }

    @Test
    @DisplayName("Test des voyages avec différentes dates")
    void testMultipleDates() {
        System.out.println("=== Test des voyages sur plusieurs dates ===");

        // Tester pour chaque jour de la semaine
        for (int day = 17; day <= 23; day++) {
            LocalDate date = LocalDate.of(2025, Month.MARCH, day);
            System.out.println("\nRecherche de voyages pour le " + date + " (" + date.getDayOfWeek() + "):");

            try {
                // Charger le profil
                Profile profile = readProfile(timeTable, date, LAUSANNE_STATION_ID);

                // Extraire les voyages
                List<Journey> journeys = extractor.journeys(profile, BERN_STATION_ID);

                // Vérifications
                assertFalse(journeys.isEmpty(), "La liste des voyages ne devrait pas être vide");
                System.out.println("Nombre de voyages trouvés: " + journeys.size());

                // Afficher le premier et le dernier voyage
                if (!journeys.isEmpty()) {
                    System.out.println("Premier voyage: " +
                            journeys.get(0).depTime().toLocalTime() + " → " +
                            journeys.get(0).arrTime().toLocalTime());

                    System.out.println("Dernier voyage: " +
                            journeys.get(journeys.size() - 1).depTime().toLocalTime() + " → " +
                            journeys.get(journeys.size() - 1).arrTime().toLocalTime());
                }
            } catch (IOException e) {
                System.out.println("⚠ Profil non disponible pour cette date: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Test de la méthode extractConnectionId")
    void testExtractConnectionId() throws IOException {
        System.out.println("=== Test de l'extraction de l'ID de connexion ===");

        // Charger le profil
        Profile profile = readProfile(timeTable, TEST_DATE, GRUYERES_STATION_ID);

        // Extraire les voyages
        List<Journey> journeys = extractor.journeys(profile, EPFL_STATION_ID);
        assertFalse(journeys.isEmpty(), "La liste des voyages ne devrait pas être vide");

        // Analyser la structure d'un voyage pour voir si les connexions sont correctement extraites
        Journey journey = journeys.get(0);
        List<Journey.Leg> legs = journey.legs();

        // Compter les étapes de transport qui représentent des connexions
        long transportCount = legs.stream()
                .filter(leg -> leg instanceof Journey.Leg.Transport)
                .count();

        System.out.println("Voyage de " + journey.depStop().name() + " à " + journey.arrStop().name());
        System.out.println("Nombre d'étapes de transport (connexions): " + transportCount);

        // Vérifier que le voyage contient au moins une connexion
        assertTrue(transportCount > 0, "Le voyage devrait contenir au moins une connexion");

        // Imprimer les détails des étapes de transport
        System.out.println("\nDétails des connexions:");
        legs.stream()
                .filter(leg -> leg instanceof Journey.Leg.Transport)
                .map(leg -> (Journey.Leg.Transport) leg)
                .forEach(transport -> {
                    System.out.printf("- De %s à %s sur %s %s (%s)%n",
                            transport.depStop().name(),
                            transport.arrStop().name(),
                            transport.vehicle(),
                            transport.route(),
                            transport.destination());
                });
    }

    @Test
    @DisplayName("Test du tri des voyages")
    void testJourneySorting() throws IOException {
        System.out.println("=== Test du tri des voyages ===");

        // Charger le profil
        Profile profile = readProfile(timeTable, TEST_DATE, BERN_STATION_ID);

        // Extraire les voyages
        List<Journey> journeys = extractor.journeys(profile, LAUSANNE_STATION_ID);

        // Vérifier que nous avons des voyages
        assertFalse(journeys.isEmpty(), "La liste des voyages ne devrait pas être vide");
        System.out.println("Nombre de voyages trouvés: " + journeys.size());

        // Vérifier que les voyages sont triés par heure de départ
        boolean isSorted = true;
        for (int i = 1; i < journeys.size(); i++) {
            Journey prev = journeys.get(i - 1);
            Journey curr = journeys.get(i);

            if (prev.depTime().isAfter(curr.depTime())) {
                isSorted = false;
                System.out.printf("Erreur de tri: Voyage #%d (%s) est après Voyage #%d (%s)%n",
                        i - 1, prev.depTime(), i, curr.depTime());
            }

            // Si même heure de départ, vérifier l'heure d'arrivée
            if (prev.depTime().equals(curr.depTime()) && prev.arrTime().isAfter(curr.arrTime())) {
                isSorted = false;
                System.out.printf("Erreur de tri (même départ): Voyage #%d (arrivée: %s) est après Voyage #%d (arrivée: %s)%n",
                        i - 1, prev.arrTime(), i, curr.arrTime());
            }
        }

        assertTrue(isSorted, "Les voyages devraient être triés par heure de départ puis par heure d'arrivée");
        System.out.println("✓ Les voyages sont correctement triés");

        // Afficher quelques voyages pour vérification
        System.out.println("\nExemples de voyages triés:");
        for (int i = 0; i < Math.min(5, journeys.size()); i++) {
            Journey j = journeys.get(i);
            System.out.printf("Voyage #%d: Départ %s, Arrivée %s%n",
                    i, j.depTime().toLocalTime(), j.arrTime().toLocalTime());
        }
    }

    @Test
    @DisplayName("Test des arrêts intermédiaires")
    void testIntermediateStops() throws IOException {
        System.out.println("=== Test des arrêts intermédiaires ===");

        // Charger le profil
        Profile profile = readProfile(timeTable, TEST_DATE, ZURICH_HB_STATION_ID);

        // Extraire les voyages

        System.out.println(profile.timeTable().stations().name(GENEVE_STATION_ID));
        List<Journey> journeys = extractor.journeys(profile, GENEVE_STATION_ID);

        // Vérifier que nous avons des voyages
        assertFalse(journeys.isEmpty(), "La liste des voyages ne devrait pas être vide");

        // Trouver un voyage avec des arrêts intermédiaires
        Journey journeyWithIntermediateStops = null;
        for (Journey journey : journeys) {
            for (Journey.Leg leg : journey.legs()) {
                if (leg instanceof Journey.Leg.Transport) {
                    Journey.Leg.Transport transport = (Journey.Leg.Transport) leg;
                    if (!transport.intermediateStops().isEmpty()) {
                        journeyWithIntermediateStops = journey;
                        break;
                    }
                }
            }
            if (journeyWithIntermediateStops != null) break;
        }

        if (journeyWithIntermediateStops != null) {
            System.out.println("Voyage trouvé avec arrêts intermédiaires:");
            printJourneyDetails(journeyWithIntermediateStops);

            // Compter les arrêts intermédiaires
            int totalIntermediateStops = 0;
            for (Journey.Leg leg : journeyWithIntermediateStops.legs()) {
                if (leg instanceof Journey.Leg.Transport) {
                    Journey.Leg.Transport transport = (Journey.Leg.Transport) leg;
                    totalIntermediateStops += transport.intermediateStops().size();

                    if (!transport.intermediateStops().isEmpty()) {
                        System.out.printf("\nEtape de %s à %s avec %d arrêts intermédiaires:%n",
                                transport.depStop().name(),
                                transport.arrStop().name(),
                                transport.intermediateStops().size());

                        for (int i = 0; i < transport.intermediateStops().size(); i++) {
                            Journey.Leg.IntermediateStop stop = transport.intermediateStops().get(i);
                            System.out.printf("  %d. %s (Arrivée: %s, Départ: %s)%n",
                                    i + 1,
                                    stop.stop().name(),
                                    stop.arrTime().toLocalTime(),
                                    stop.depTime().toLocalTime());
                        }
                    }
                }
            }

            System.out.println("\nTotal des arrêts intermédiaires: " + totalIntermediateStops);
            assertTrue(totalIntermediateStops > 0, "Le voyage devrait avoir des arrêts intermédiaires");
        } else {
            System.out.println("⚠ Aucun voyage avec arrêts intermédiaires trouvé");
        }
    }

    // Méthodes utilitaires

    private void printJourneyDetails(Journey journey) {
        System.out.printf("Voyage de %s à %s%n", journey.depStop().name(), journey.arrStop().name());
        System.out.printf("Départ: %s, Arrivée: %s (Durée: %d minutes)%n",
                journey.depTime().toLocalTime(),
                journey.arrTime().toLocalTime(),
                java.time.Duration.between(journey.depTime(), journey.arrTime()).toMinutes());

        System.out.println("Étapes du voyage:");
        for (int i = 0; i < journey.legs().size(); i++) {
            Journey.Leg leg = journey.legs().get(i);
            if (leg instanceof Journey.Leg.Transport) {
                Journey.Leg.Transport transport = (Journey.Leg.Transport) leg;
                System.out.printf("  %d. Transport: %s → %s sur %s %s (%s)%n",
                        i + 1,
                        transport.depStop().name(),
                        transport.arrStop().name(),
                        transport.vehicle(),
                        transport.route(),
                        transport.destination());
            } else if (leg instanceof Journey.Leg.Foot) {
                Journey.Leg.Foot foot = (Journey.Leg.Foot) leg;
                System.out.printf("  %d. À pied: %s → %s%n",
                        i + 1,
                        foot.depStop().name(),
                        foot.arrStop().name());
            }
        }
    }

    private void verifyJourneyStructure(Journey journey) {
        // Vérifier que le voyage a des étapes
        List<Journey.Leg> legs = journey.legs();
        assertFalse(legs.isEmpty(), "Le voyage devrait avoir des étapes");

        // Vérifier l'alternance des étapes (Transport puis Foot, puis Transport, etc.)
        for (int i = 0; i < legs.size() - 1; i++) {
            Journey.Leg currentLeg = legs.get(i);
            Journey.Leg nextLeg = legs.get(i + 1);

            if (currentLeg instanceof Journey.Leg.Transport) {
                assertTrue(nextLeg instanceof Journey.Leg.Foot,
                        "Une étape de transport devrait être suivie d'une étape à pied");
            } else {
                assertTrue(nextLeg instanceof Journey.Leg.Transport,
                        "Une étape à pied devrait être suivie d'une étape de transport");
            }
        }

        // Vérifier que les étapes sont liées (l'arrivée d'une étape est le départ de la suivante)
        for (int i = 0; i < legs.size() - 1; i++) {
            Journey.Leg currentLeg = legs.get(i);
            Journey.Leg nextLeg = legs.get(i + 1);

            assertEquals(currentLeg.arrStop().name(), nextLeg.depStop().name(),
                    "L'arrivée d'une étape devrait être le départ de la suivante");

            assertTrue(currentLeg.arrTime().compareTo(nextLeg.depTime()) <= 0,
                    "L'heure d'arrivée d'une étape devrait être avant ou égale à l'heure de départ de la suivante");
        }

        // Vérifier que pour chaque étape, l'heure de départ est avant l'heure d'arrivée
        for (Journey.Leg leg : legs) {
            assertTrue(leg.depTime().compareTo(leg.arrTime()) <= 0,
                    "L'heure de départ d'une étape devrait être avant ou égale à son heure d'arrivée");
        }

        // Vérifier les arrêts intermédiaires dans les étapes de transport
        for (Journey.Leg leg : legs) {
            if (leg instanceof Journey.Leg.Transport) {
                Journey.Leg.Transport transport = (Journey.Leg.Transport) leg;

                if (!transport.intermediateStops().isEmpty()) {
                    // Le premier arrêt intermédiaire doit être après le départ
                    assertTrue(transport.depTime().compareTo(transport.intermediateStops().get(0).arrTime()) <= 0,
                            "Le premier arrêt intermédiaire devrait être après le départ");

                    // Le dernier arrêt intermédiaire doit être avant l'arrivée
                    int lastIndex = transport.intermediateStops().size() - 1;
                    assertTrue(transport.intermediateStops().get(lastIndex).depTime().compareTo(transport.arrTime()) <= 0,
                            "Le dernier arrêt intermédiaire devrait être avant l'arrivée");

                    // Les arrêts intermédiaires doivent être dans l'ordre
                    for (int i = 0; i < transport.intermediateStops().size() - 1; i++) {
                        Journey.Leg.IntermediateStop current = transport.intermediateStops().get(i);
                        Journey.Leg.IntermediateStop next = transport.intermediateStops().get(i + 1);

                        assertTrue(current.depTime().compareTo(next.arrTime()) <= 0,
                                "Les arrêts intermédiaires devraient être dans l'ordre chronologique");
                    }
                }
            }
        }
    }

    private Profile readProfile(TimeTable timeTable, LocalDate date, int arrStationId) throws IOException {
        Path path = Path.of("profile_" + date + "_" + "11486.txt");
        if (!Files.exists(path)) {
            throw new IOException("Fichier de profil non trouvé: " + path.toAbsolutePath());
        }

        try (BufferedReader r = Files.newBufferedReader(path)) {
            Profile.Builder profileB = new Profile.Builder(timeTable, date, arrStationId);
            int stationId = -1;
            String line;

            while ((line = r.readLine()) != null) {
                stationId += 1;
                if (line.isEmpty()) continue;

                ParetoFront.Builder frontB = new ParetoFront.Builder();
                for (String t : line.split(","))
                    if (!t.trim().isEmpty())
                        frontB.add(Long.parseLong(t.trim(), 16));

                profileB.setForStation(stationId, frontB);
            }

            return profileB.build();
        }
    }
}