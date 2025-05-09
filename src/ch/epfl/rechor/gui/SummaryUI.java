package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record SummaryUI(Node rootNode, ObservableValue<Journey> selectedJourneyO) {

    public static SummaryUI create(ObservableValue<List<Journey>> allJourney, ObservableValue<LocalTime> hour)
    {

        ListView<Journey> allElements = new ListView<>();
        allElements.setCellFactory(listView -> new journeyCellList());
        allJourney.getValue().forEach(journey -> allElements.getItems().add(journey));
        allElements.getStylesheets().add("summary.css");
        int id = -1;
        int currentIndex = 0;
        do {
            if(currentIndex == allJourney.getValue().size())id = currentIndex-1;
            else if(!allJourney.getValue().get(currentIndex).depTime().toLocalTime().isBefore(hour.getValue()))id = currentIndex;

            currentIndex++;
        }while (id == -1);
        ObservableValue<Journey> currentJourney = new SimpleObjectProperty<>(allJourney.getValue().get(id));
        allElements.getSelectionModel().select(id);
        allElements.scrollTo(id);
        return new SummaryUI(allElements, currentJourney);
    }

    private static class journeyCellList extends ListCell<Journey> {
        private final Text departure = new Text();
        private final Text arrival = new Text();
        private final Text direction = new Text();
        private final Text duration = new Text();
        private final ImageView imageView = new ImageView();
        private final BorderPane journeyPane = new BorderPane();
        private final List<Circle> circle = new ArrayList<>();
        private final Pane linePane = new Pane(){
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                getChildren().clear();
                setPrefSize(0,0);
                Line line = new Line(5,getHeight()/2,getWidth()-5,getHeight()/2);
                getChildren().add(line);
                Circle circle1 = new Circle(3);
                circle1.setCenterX(line.getStartX());
                circle1.setCenterY(line.getStartY());
                circle1.getStyleClass().add("dep-arr");
                Circle circle2 = new Circle(3);
                circle2.setCenterX(line.getEndX());
                circle2.setCenterY(line.getEndY());
                circle2.getStyleClass().add("dep-arr");
                getChildren().add(circle1);
                getChildren().add(circle2);
                circle.forEach(circle3 -> {
                        double position = (double) circle3.getUserData()*(getWidth()-2*5) + 5;
                        circle3.setCenterX(position);
                        circle3.setCenterY(getHeight()/2);
                        getChildren().add(circle3);
                });
            }
        };
        


        public journeyCellList()
        {
            journeyPane.getStyleClass().add("journey");
            departure.getStyleClass().add("departure");
            HBox route = new HBox(imageView, direction);
            route.getStyleClass().add("route");
            HBox dura = new HBox(duration);
            dura.getStyleClass().add("duration");
            journeyPane.setTop(route);
            journeyPane.setBottom(dura);
            journeyPane.setLeft(departure);
            journeyPane.setRight(arrival);
            journeyPane.setCenter(linePane);
        }

        @Override
        protected void updateItem(Journey item, boolean empty) {
            super.updateItem(item, empty);
            int transportIndex;
            circle.clear();
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                departure.setText(FormatterFr.formatTime(item.depTime()));
                arrival.setText(FormatterFr.formatTime(item.arrTime()));
                duration.setText(FormatterFr.formatDuration(item.duration()));
                if(item.legs().getFirst() instanceof Journey.Leg.Transport)
                    transportIndex = 0;
                else{
                    transportIndex = 1;
                }
                for(int i= transportIndex+1; i<item.legs().size(); i+=2)
                {
                    Circle miniCircle = new Circle(3);
                    miniCircle.getStyleClass().add("transfer");
                    Duration totalDuration = Duration.between(item.depTime(),item.legs().get(i).depTime());
                    double position = (double) totalDuration.toMinutes() /item.duration().toMinutes();
                    miniCircle.setUserData(position);
                    circle.add(miniCircle);
                }
                direction.setText(FormatterFr.formatRouteDestination((Journey.Leg.Transport) item.legs().get(transportIndex)));
                imageView.setImage(VehicleIcons.IconFor(((Journey.Leg.Transport) item.legs().get(transportIndex)).vehicle()));
                imageView.setFitHeight(20);
                imageView.setFitWidth(20);
                setGraphic(journeyPane);
            }
        }
    }
}
