package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Main extends Application {
    private ObservableValue<List<Journey>> journeys0;
    private Profile profile;


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
        List<String> Name = new ArrayList<>();
        Map<String, String> aliases = new HashMap<>();
        for(int i=0; i < timeTable.stationAliases().size(); i++) {
            aliases.put(timeTable.stationAliases().stationName(i), timeTable.stationAliases().alias(i));
        }

        for(int i=0; i < timeTable.stations().size(); i++) {
            Name.add(timeTable.stations().name(i));
        }

        StopIndex stopIndex = new StopIndex(Name, aliases);
        QueryUI queryUI = QueryUI.create(stopIndex);

        ObservableValue<String> arrStop0 = queryUI.arrStopO();
        ObservableValue<String> depStop0 = queryUI.depStopO();
        ObservableValue<LocalDate> date0 = queryUI.dateO();


        journeys0 = Bindings.createObjectBinding(() ->
        {

            int arrStationId = stationId(stations, arrStop0.getValue());
            LocalDate date = date0.getValue();

            int depStationId = stationId(stations, depStop0.getValue());
            if(profile!= null && profile.arrStationId() == arrStationId && profile.date().equals(date)){
                return JourneyExtractor
                        .journeys(profile, depStationId);
            }
            Router router = new Router(timeTable);
            List<Journey> journeys = null;


            if(arrStationId != -1) {
                profile = router.profile(date, arrStationId);
                journeys= JourneyExtractor
                        .journeys(profile, depStationId);
            }
            return journeys;
            },arrStop0,depStop0, date0
       );

        ObservableValue<LocalTime> depTimeO = queryUI.timeO();
        SummaryUI summaryUI = SummaryUI.create(journeys0, depTimeO);
        DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());
        SplitPane pane = new SplitPane(summaryUI.rootNode(),detailUI.rootNode());
        summaryUI.selectedJourneyO().addListener((obs, oldValue, newValue) -> {
            if(oldValue != newValue){
                pane.getItems().clear();
                DetailUI detailU = DetailUI.create(summaryUI.selectedJourneyO());
                pane.getItems().add(summaryUI.rootNode());
                pane.getItems().add(detailU.rootNode());
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
