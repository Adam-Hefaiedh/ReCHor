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
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

/**
 * La classe principale du projet liant toutes les interfaces.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class Main extends Application {
    private ObservableValue<List<Journey>> journeys0;
    private Profile cachedProfile;

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

        ObservableValue<String> arrStop0 = queryUI.arrStopO();
        ObservableValue<String> depStop0 = queryUI.depStopO();
        ObservableValue<LocalDate> date0 = queryUI.dateO();


        journeys0 = Bindings.createObjectBinding(() ->
        {

            int arrStationId = stationId(stations, arrStop0.getValue());
            LocalDate date = date0.getValue();

            int depStationId = stationId(stations, depStop0.getValue());
            if(cachedProfile!= null && cachedProfile.arrStationId() == arrStationId && cachedProfile.date().equals(date)){
                return JourneyExtractor
                        .journeys(cachedProfile, depStationId);
            }
            Router router = new Router(timeTable);
            List<Journey> journeys = Collections.emptyList();


            if(arrStationId != -1) {
                cachedProfile = router.profile(date, arrStationId);
                journeys= JourneyExtractor
                        .journeys(cachedProfile, depStationId);
            }
            return journeys;
            },arrStop0,depStop0, date0
       );

        ObservableValue<LocalTime> depTimeO = queryUI.timeO();
        SummaryUI summaryUI = SummaryUI.create(journeys0, depTimeO);
        DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());
        SplitPane pane = new SplitPane(summaryUI.rootNode(),detailUI.rootNode());
        summaryUI.selectedJourneyO().addListener((obs, oldJourney, newJourney) -> {
            if(oldJourney != newJourney){
                DetailUI updatedDetailUI = DetailUI.create(summaryUI.selectedJourneyO());
                pane.getItems().set(1,updatedDetailUI.rootNode());
            }
        });

       BorderPane root = new BorderPane();

       root.setCenter(pane);
       root.setTop(queryUI.rootNode());
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.setMinWidth(800);
       stage.setMinHeight(600);
       stage.setTitle("ReCHor");
       stage.show();
        Platform.runLater(() ->
                scene.lookup("#depStop").requestFocus());
    }

}
