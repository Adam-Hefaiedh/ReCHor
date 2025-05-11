package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;


import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe représente la partie de l'interface graphique qui montre la vue d'ensemble des voyages.
 * @param rootNode nœud JavaFX à la racine de son graphe de scène.
 * @param selectedJourneyO valeur contenant le voyage sélectionné dans la vue d'ensemble.
 */
public record SummaryUI(Node rootNode, ObservableValue<Journey> selectedJourneyO) {
    private static boolean change = false;
    /**
     * Permet de créer le graphe de scène.
     * @param allJourney valeur contenant la liste des voyages à afficher.
     * @param hour valeur contenant l'heure de voyage désirée.
     *
     * @return une instance de SummaryUI contenant sa racine ainsi
     * que la valeur observable contenant le voyage sélectionné.
     */
    public static SummaryUI create(ObservableValue<List<Journey>> allJourney, ObservableValue<LocalTime> hour)
    {
        List<Journey> journeys = allJourney.getValue();
        LocalTime currentTime = hour.getValue();
        ListView<Journey> allElements = new ListView<>();
        allElements.setCellFactory(listView -> new journeyCellList());
        if(journeys != null) {
            journeys.forEach(journey -> allElements.getItems().add(journey));

            allElements.getStylesheets().add("summary.css");
            int id = findSelectedJourney(journeys, currentTime);
            allElements.getSelectionModel().select(id);
            allElements.scrollTo(id);

            ObservableValue<Journey> currentJourney = allElements.getSelectionModel().selectedItemProperty();
            allElements.getSelectionModel().selectedItemProperty().addListener((obs, oldJourney, newJourney) -> {
                if (oldJourney != newJourney && newJourney != null) {
                    change = true;
                }
            });

            hour.addListener((obs, oldTime, newTime) -> {
                if (!change) {
                    int index = findSelectedJourney(journeys, newTime);
                    allElements.getSelectionModel().select(index);
                    allElements.scrollTo(index);
                }
            });

            return new SummaryUI(allElements, currentJourney);
        }
        return new SummaryUI(allElements, new SimpleObjectProperty<>(null));
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
                Circle depCircle = new Circle(3);
                depCircle.setCenterX(line.getStartX());
                depCircle.setCenterY(line.getStartY());
                depCircle.getStyleClass().add("dep-arr");
                Circle arrCircle = new Circle(3);
                arrCircle.setCenterX(line.getEndX());
                arrCircle.setCenterY(line.getEndY());
                arrCircle.getStyleClass().add("dep-arr");
                getChildren().add(depCircle);
                getChildren().add(arrCircle);
                circle.forEach(middleCircle -> {
                        double position = (double) middleCircle.getUserData()*(getWidth()-2*5) + 5;
                        middleCircle.setCenterX(position);
                        middleCircle.setCenterY(getHeight()/2);
                        getChildren().add(middleCircle);
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
                List<Journey.Leg> legs = item.legs();
                departure.setText(FormatterFr.formatTime(item.depTime()));
                arrival.setText(FormatterFr.formatTime(item.arrTime()));
                duration.setText(FormatterFr.formatDuration(item.duration()));
                if(legs.getFirst() instanceof Journey.Leg.Transport)
                    transportIndex = 0;
                else{
                    transportIndex = 1;
                }
                for(int i= transportIndex+1; i<legs.size()-1; i+=2)
                {
                    Circle middleCircle = new Circle(3);
                    middleCircle.getStyleClass().add("transfer");
                    Duration totalDuration = Duration.between(item.depTime(),legs.get(i).depTime());
                    double position = (double) totalDuration.toMinutes() /item.duration().toMinutes();
                    middleCircle.setUserData(position);
                    circle.add(middleCircle);
                }
                direction.setText(FormatterFr.formatRouteDestination((Journey.Leg.Transport) legs.get(transportIndex)));
                imageView.setImage(VehicleIcons.IconFor(((Journey.Leg.Transport) legs.get(transportIndex)).vehicle()));
                imageView.setFitHeight(20);
                imageView.setFitWidth(20);
                setGraphic(journeyPane);
            }
        }
    }
    
    private static int findSelectedJourney(List<Journey> allJourney, LocalTime hour)
    {
        int id = -1;
        int currentIndex = 0;
        do {
            if(currentIndex == allJourney.size())id = currentIndex-1;
            else if(!allJourney.get(currentIndex).depTime().toLocalTime().isBefore(hour))id = currentIndex;

            currentIndex++;
        }while (id == -1);
        return id;
    }
}
