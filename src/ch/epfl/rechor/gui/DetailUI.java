package ch.epfl.rechor.gui;


import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyGeoJsonConverter;
import ch.epfl.rechor.journey.JourneyIcalConverter;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * La classe représente la partie de l'interface graphique qui montre les détails d'un voyage.
 * @param rootNode est le nœud JavaFX à la racine de son graphe de scène.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public record DetailUI (Node rootNode) {

    /**
     * Permet de créer le graphe de scène
     * @param journey La valeur contient le voyage dont les détails doivent être affichés dans l'interface graphique.
     *
     * @return une instance de DetailUI contenant une référence à sa racine.
     */
    public static DetailUI create(ObservableValue<Journey> journey) {
        ScrollPane detailPane = new ScrollPane();
        detailPane.setId("detail");
        detailPane.getStylesheets().add("detail.css");

        Label noneLabel = new Label("Aucun voyage");
        VBox noJourney = new VBox(noneLabel);
        noJourney.setId("no-journey");
        List<Pair<Circle,Circle>> circles = new ArrayList<>();

        GridPane stepsPane = new GridPane();
        stepsPane.setId("legs");

        Pane Annotation = getPane(circles);

        journey.subscribe((newJourney) -> {
                    circles.clear();
                    stepsPane.getChildren().clear();
                    createStepsPane(newJourney, circles, stepsPane);

                    Annotation.requestLayout();
        });

        Journey currentJourney = journey.getValue();

        VBox Page = getPage(currentJourney, Annotation, stepsPane);

        StackPane window = new StackPane(noJourney, Page);
        Page.setVisible(currentJourney != null);
        noJourney.setVisible(currentJourney == null);
        detailPane.setContent(window);
        return new DetailUI(detailPane);
    }

    private static Pane getPane(List<Pair<Circle, Circle>> circles) {
        Pane Annotation = new Pane(){
            @Override
            protected void layoutChildren() {
                getChildren().removeIf(node -> node instanceof Line);

                super.layoutChildren();
                for (Pair<Circle, Circle> pair : circles) {
                    Circle depCircle = pair.getKey();
                    Circle arrCircle = pair.getValue();

                    double startX = depCircle.getBoundsInParent().getCenterX();
                    double startY = depCircle.getBoundsInParent().getCenterY();
                    double endX = arrCircle.getBoundsInParent().getCenterX();
                    double endY = arrCircle.getBoundsInParent().getCenterY();

                    Line line = new Line(startX, startY, endX, endY);
                    line.setStroke(Color.RED);
                    line.setStrokeWidth(2);

                    getChildren().add(line);
                }
            }
        };
        Annotation.setId("annotations");
        return Annotation;
    }

    private static void createStepsPane(Journey journey, List<Pair<Circle,Circle>> circles, GridPane stepsPane) {
        List<Journey.Leg> steps = new ArrayList<>();
        if(journey != null)  steps = journey.legs();
        AtomicInteger rowIndex= new AtomicInteger(0);
        steps.forEach((leg) -> {
            switch (leg) {
                case Journey.Leg.Foot f : {
                    Text transfer = new Text(FormatterFr.formatLeg((Journey.Leg.Foot) leg));
                    stepsPane.add(transfer, 2, rowIndex.get(), 2, 1);
                    rowIndex.getAndIncrement();
                    break;
                }
                case Journey.Leg.Transport t : {

                    Text depTime = new Text(FormatterFr.formatTime(leg.depTime()));
                    Circle depCircle = new Circle(3);
                    Text depStop = new Text(leg.depStop().name());
                    Text depPlatform = new Text(FormatterFr.formatPlatformName(leg.depStop()));
                    depPlatform.getStyleClass().add("departure");
                    depTime.getStyleClass().add("departure");
                    stepsPane.add(depTime, 0, rowIndex.get());
                    stepsPane.add(depCircle, 1, rowIndex.get());
                    stepsPane.add(depStop, 2, rowIndex.get());
                    stepsPane.add(depPlatform, 3, rowIndex.get());
                    rowIndex.getAndIncrement();


                    ImageView vehicleImage = createVehiculeIcon(VehicleIcons.IconFor(((Journey.Leg.Transport) leg).vehicle()));
                    Text routeDest = new Text(FormatterFr.formatRouteDestination((Journey.Leg.Transport) leg));
                    stepsPane.add(routeDest, 2, rowIndex.get(), 2, 1);


                    if (!leg.intermediateStops().isEmpty()) {
                        stepsPane.add(vehicleImage, 0, rowIndex.get(), 1, 2);
                        rowIndex.getAndIncrement();
                        intermediateStopView(leg,stepsPane,rowIndex);

                    } else {
                        stepsPane.add(vehicleImage, 0, rowIndex.get(), 1, 1);
                        rowIndex.getAndIncrement();
                    }


                    Text arrTime = new Text(FormatterFr.formatTime(leg.arrTime()));
                    Circle arrCircle = new Circle(3);
                    Text arrStop = new Text(leg.arrStop().name());
                    Text arrPlatform = new Text(FormatterFr.formatPlatformName(leg.arrStop()));
                    stepsPane.add(arrTime, 0, rowIndex.get());
                    stepsPane.add(arrCircle, 1, rowIndex.get());
                    stepsPane.add(arrStop, 2, rowIndex.get());
                    stepsPane.add(arrPlatform, 3, rowIndex.get());
                    circles.add(new Pair<>(depCircle, arrCircle));
                    rowIndex.getAndIncrement();
                    break;
                }

            }
        });

    }

    private static void intermediateStopView(Journey.Leg leg, GridPane stepsPane, AtomicInteger rowIndex){
        Accordion interStops = new Accordion();
        TitledPane titledPane = new TitledPane();
        titledPane.setText(leg.intermediateStops().size() + " arrêts, " + FormatterFr.formatDuration(leg.duration()));

        GridPane interStopsGrid = new GridPane();
        interStopsGrid.setHgap(5);
        interStopsGrid.setVgap(2);
        AtomicInteger rowIndex2 = new AtomicInteger();
        leg.intermediateStops().forEach((i) -> {
            interStopsGrid.add(new Text(FormatterFr.formatTime(i.arrTime())), 0, rowIndex2.get());
            interStopsGrid.add(new Text(FormatterFr.formatTime(i.depTime())), 1, rowIndex2.get());
            interStopsGrid.add(new Text(i.stop().name()), 2, rowIndex2.get());
            rowIndex2.getAndIncrement();
        });

        titledPane.setContent(interStopsGrid);
        interStops.getPanes().add(titledPane);
        stepsPane.add(interStops, 2, rowIndex.get(), 2, 1);
        rowIndex.getAndIncrement();
    }

    private static ImageView createVehiculeIcon(Image image)
    {
        ImageView vehicleImage = new ImageView(image);
        vehicleImage.setFitWidth(31);
        vehicleImage.setFitHeight(31);
        vehicleImage.setPreserveRatio(true);
        return vehicleImage;
    }

    private static VBox getPage(Journey journey, Pane Annotation, GridPane stepsPane) {
        StackPane Page = new StackPane(Annotation, stepsPane);
        Button map = createFunctionnalButton(event -> {
            try {
                URI link = new URI("https","umap.osm.ch","/fr/map", "data="+ JourneyGeoJsonConverter.toGeoJSON(journey), null);
                Desktop.getDesktop().browse(link);
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }, "Carte");

        Button calendar = createFunctionnalButton(event -> {
            try {
                FileChooser file = new FileChooser();
                file.setTitle("format iCalendar");
                file.setInitialFileName("voyage_" + journey.depTime().toLocalDate() + ".ics");

                File selectedFile = file.showSaveDialog(Page.getScene().getWindow());
                Files.writeString(selectedFile.toPath(), JourneyIcalConverter.toIcalendar(journey));
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        }, "Calendrier");

        HBox buttonBar = new HBox(2, map, calendar);
        buttonBar.setId("buttons");
        return new VBox(5, Page, buttonBar);
    }

    private static Button createFunctionnalButton(EventHandler<ActionEvent> action, String title){
        Button calendar = new Button(title);
        calendar.setOnAction(action);
        return calendar;
    }
}

