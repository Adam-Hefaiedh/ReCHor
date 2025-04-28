package ch.epfl.gui_debug;

import ch.epfl.rechor.gui.DetailUI;
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
import java.time.Month;

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
        Stations stations = timeTable.stations();
        LocalDate date = LocalDate.of(2025, Month.APRIL, 15);
        int depStationId = stationId(stations, "Ecublens VD, EPFL");
        int arrStationId = stationId(stations, "Gruyères");
        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);
        Journey journey = JourneyExtractor
                .journeys(profile, depStationId)
                .get(32);
        ObservableValue<Journey> journeyO =
                new SimpleObjectProperty<>(journey);
        DetailUI detailUI = DetailUI.create(journeyO);
        Pane root = new BorderPane(detailUI.rootNode());
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
}