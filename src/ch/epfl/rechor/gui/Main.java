package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    static int stationId(Stations stations, String stationName) {
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
        LocalDate date = LocalDate.of(2025, Month.MAY, 5);
        int depStationId = stationId(stations, "Lausanne, Ours");
        int arrStationId = stationId(stations, "Ins");
        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);
        List<Journey> journeys = JourneyExtractor
                .journeys(profile, depStationId);

        ObservableValue<List<Journey>> journeysO =
                new SimpleObjectProperty<>(journeys);
        ObservableValue<LocalTime> depTimeO =
                new SimpleObjectProperty<>(LocalTime.of(12, 50));
        SummaryUI summaryUI = SummaryUI.create(journeysO, depTimeO);
        DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());
       BorderPane root = new BorderPane();
       SplitPane pane = new SplitPane(summaryUI.rootNode(), detailUI.rootNode());
       root.setCenter(pane);
       Scene scene = new Scene(root);
       stage.setScene(scene);
       stage.setMinWidth(800);
       stage.setMinHeight(600);
       stage.setTitle("ReCHor");
       stage.show();
    }
}
