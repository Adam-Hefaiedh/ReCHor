package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.StationAliases;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * La classe principale du projet liant toutes les interfaces.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class Main extends Application {
    private final ObjectProperty<List<Journey>> journeysProperty = new SimpleObjectProperty<>(Collections.emptyList());
    private Profile cachedProfile;
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Lance l'interface de voyage à l'aide de la methode launch
     * @param args parametre à mettre en argument dans la methode launch
     */
    public static void main(String[] args) {
        launch(args);
    }


    private static int stationId(Stations stations, String stationName) {
        for(int i=0; i<stations.size(); i++) {
            if(stations.name(i).equals(stationName)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void start(Stage stage) throws Exception {
        TimeTable timeTable = new CachedTimeTable(
                FileTimeTable.in(Path.of("timetable")));
        Router router = new Router(timeTable);
        Stations stations = timeTable.stations();
        StationAliases aliasesStation = timeTable.stationAliases();
        List<String> stationName = new ArrayList<>();
        Map<String, String> aliases = new HashMap<>();
        for(int i=0; i < timeTable.stationAliases().size(); i++) {
            aliases.put(aliasesStation.alias(i), aliasesStation.stationName(i));
        }

        for(int i=0; i < timeTable.stations().size(); i++) {
            stationName.add(stations.name(i));
        }

        StopIndex stopIndex = new StopIndex(stationName, aliases);
        QueryUI queryUI = QueryUI.create(stopIndex);
        Image loadingGif = new Image("loading.gif");
        ImageView loadingImageView = new ImageView(loadingGif);
        loadingImageView.setFitWidth(100);
        loadingImageView.setFitHeight(100);
        loadingImageView.setPreserveRatio(true);
        Label loadingLabel = new Label("Chargement en cours...");
        VBox loadingBox = new VBox(10, loadingImageView, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.visibleProperty().bind(loading);


        Runnable searchListener = () -> {

                String arrStopName = queryUI.arrStopO().getValue();
                String depStopName = queryUI.depStopO().getValue();
                LocalDate date = queryUI.dateO().getValue();

                if (arrStopName.isEmpty() ||
                        depStopName.isEmpty()) {
                    journeysProperty.set(Collections.emptyList());
                    return;
                }

                int arrStationId = stationId(stations, arrStopName);
                int depStationId = stationId(stations, depStopName);

                if (arrStationId == -1 || depStationId == -1) {
                    journeysProperty.set(Collections.emptyList());
                    return;
                }

                if (cachedProfile != null &&
                        cachedProfile.arrStationId() == arrStationId &&
                        cachedProfile.date().equals(date)) {
                    List<Journey> journeys = JourneyExtractor.journeys(cachedProfile, depStationId);
                    journeysProperty.set(journeys);
                    return;
                }

                loading.set(true);
                journeysProperty.set(Collections.emptyList());

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call(){
                        cachedProfile = router.profile(date, arrStationId);
                        List<Journey> journeys = JourneyExtractor.journeys(cachedProfile, depStationId);

                        Platform.runLater(() -> {
                            journeysProperty.set(journeys);
                            loading.set(false);
                        });

                        return null;
                    }
                };
                executorService.submit(task);




        };

        queryUI.research().setOnAction(event -> searchListener.run());



        ObservableValue<LocalTime> depTimeO = queryUI.timeO();
        SummaryUI summaryUI = SummaryUI.create(journeysProperty, depTimeO);
        Node summaryUi = summaryUI.rootNode();
        StackPane summaryPane = new StackPane(summaryUi, loadingBox);
        SplitPane pane = new SplitPane(summaryPane);
        summaryUI.selectedJourneyO().subscribe((newJourney) -> {
                DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());
                if(pane.getItems().size() < 2) pane.getItems().add(detailUI.rootNode());
                else pane.getItems().set(1,detailUI.rootNode());
        });
       BorderPane root = new BorderPane();

       root.setCenter(pane);
       root.setTop(queryUI.rootNode());
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.setMinWidth(800);
       stage.setMinHeight(600);
       stage.setTitle("ReCHor");
       Image logo = new Image("logo.png");
       stage.getIcons().add(logo);
       stage.show();
        Platform.runLater(() ->
                scene.lookup("#depStop").requestFocus());
    }

}
