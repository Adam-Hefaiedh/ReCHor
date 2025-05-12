package ch.epfl.gui_debug;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.gui.QueryUI;
import ch.epfl.rechor.gui.SummaryUI;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TestDetailUI extends Application {
    public static void main(String[] args) {launch(args); }

    static int stationId(Stations stations, String stationName) {
        for(int i=0; i<stations.size(); i++) {
            if(stations.name(i).equals(stationName)) {
                return i;
            }
        }

    return -1;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TimeTable timeTable = new CachedTimeTable(
                FileTimeTable.in(Path.of("timetable")));
        List<String> Name = new ArrayList<>();
        Map<String, String> stations = new HashMap<>();

        for(int i=0; i < timeTable.stationAliases().size(); i++) {
                stations.put(timeTable.stationAliases().stationName(i), timeTable.stationAliases().alias(i));
        }

        for(int i=0; i < timeTable.stations().size(); i++) {
            Name.add(timeTable.stations().name(i));
        }

        StopIndex stopIndex = new StopIndex(Name, stations);
        QueryUI summaryUI = QueryUI.create(stopIndex);
        Pane root = new BorderPane(summaryUI.rootNode());
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
}