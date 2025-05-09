package ch.epfl.rechor.gui;


import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyGeoJsonConverter;
import ch.epfl.rechor.journey.JourneyIcalConverter;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record DetailUI (Node rootNode) {

    public static DetailUI create(ObservableValue<Journey> journey) {
        ScrollPane detailPane = new ScrollPane();
        detailPane.setId("detail");
        detailPane.getStylesheets().add("detail.css");

        Label noneLabel = new Label("Aucun voyage");
        VBox vBox1 = new VBox(noneLabel);
        vBox1.setId("no-journey");
        List<Pair<Circle,Circle>> circles = new ArrayList<>();

        GridPane stepsPane = createStepsPane(journey.getValue(), circles);
        stepsPane.setId("legs");

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

        StackPane stackPane1 = new StackPane(Annotation, stepsPane);
        Button map = new Button("Carte");
        map.setOnAction(event -> {
            URI link = null;
            try {
                link = new URI("https","umap.osm.ch","/fr/map", "data="+ JourneyGeoJsonConverter.toGeoJSON(journey.getValue()), null);
                Desktop.getDesktop().browse(link);
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button calendar = new Button("Calendrier");
        calendar.setOnAction(event -> {
            try {
                FileChooser file = new FileChooser();
                file.setTitle("format iCalendar");
                file.setInitialFileName("voyage_" + journey.getValue().depTime().toLocalDate() + ".ics");

                File selectedFile = file.showSaveDialog(calendar.getScene().getWindow());
                Files.writeString(selectedFile.toPath(), JourneyIcalConverter.toIcalendar(journey.getValue()));
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        });

        HBox buttonBar = new HBox(2, map, calendar);
        buttonBar.setId("buttons");
        VBox vbox2 = new VBox(5, stackPane1, buttonBar);

        StackPane stackPane = new StackPane(vBox1, vbox2);

        Journey currentJourney = journey.getValue();
        if (currentJourney != null) {
            vbox2.setVisible(true);
            vBox1.setVisible(false);
        } else {
            vbox2.setVisible(false);
            vBox1.setVisible(true);
        }
        detailPane.setContent(stackPane);
        return new DetailUI(detailPane);
    }

    private static GridPane createStepsPane(Journey journey, List<Pair<Circle,Circle>> circles) {

        GridPane stepsPane = new GridPane();
        List<Journey.Leg> steps = new ArrayList<>();
        if(journey != null)  steps = journey.legs();
        AtomicInteger rowIndex= new AtomicInteger(0);
        steps.forEach((l) -> {

            if (l instanceof Journey.Leg.Foot){
                Text transfer = new Text(FormatterFr.formatLeg((Journey.Leg.Foot) l));
                stepsPane.add(transfer, 2, rowIndex.get(), 2, 1);
                rowIndex.getAndIncrement();
            }

            else if(l instanceof Journey.Leg.Transport) {

                //ligne 1
                Text depTime = new Text(FormatterFr.formatTime(l.depTime()));
                Circle circle = new Circle(3);
                Text depStop = new Text(l.depStop().name());
                Text depPlatform = new Text(FormatterFr.formatPlatformName(l.depStop()));
                depPlatform.getStyleClass().add("departure");
                depTime.getStyleClass().add("departure");
                stepsPane.add(depTime,      0, rowIndex.get());
                stepsPane.add(circle,           1, rowIndex.get());
                stepsPane.add(depStop,   2, rowIndex.get());
                stepsPane.add(depPlatform,  3, rowIndex.get());
                rowIndex.getAndIncrement();

                //ligne 2
                ImageView vehicleImage = new ImageView(VehicleIcons.IconFor(((Journey.Leg.Transport) l).vehicle()));
                vehicleImage.setFitWidth(31);
                vehicleImage.setFitHeight(31);
                vehicleImage.setPreserveRatio(true);
                Text routeDest = new Text(FormatterFr.formatRouteDestination((Journey.Leg.Transport)l));
                stepsPane.add(routeDest, 2, rowIndex.get(), 2, 1);

                //ligne 3
                if (!l.intermediateStops().isEmpty()) {
                    stepsPane.add(vehicleImage, 0, rowIndex.get(), 1, 2);
                    rowIndex.getAndIncrement();
                    Accordion interStops = new Accordion();
                    TitledPane titledPane = new TitledPane();
                    titledPane.setText(l.intermediateStops().size() + " arrêts, " + FormatterFr.formatDuration(l.duration()));

                    GridPane interStopsGrid = new GridPane();
                    interStopsGrid.setHgap(5);
                    interStopsGrid.setVgap(2);
                    AtomicInteger rowIndex2 = new AtomicInteger();
                    l.intermediateStops().forEach((i) -> {
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
                else {
                    stepsPane.add(vehicleImage, 0, rowIndex.get(), 1, 1);
                    rowIndex.getAndIncrement();
                }

                //ligne 4
                Text arrTime = new Text(FormatterFr.formatTime(l.arrTime()));
                Circle circle1 = new Circle(3);
                Text arrStop = new Text(l.arrStop().name());
                Text arrPlatform = new Text(FormatterFr.formatPlatformName(l.arrStop()));
                stepsPane.add(arrTime,      0, rowIndex.get());
                stepsPane.add(circle1,           1, rowIndex.get());
                stepsPane.add(arrStop,   2, rowIndex.get());
                stepsPane.add(arrPlatform,  3, rowIndex.get());
                circles.add(new Pair<>(circle,circle1));
                rowIndex.getAndIncrement();
            }
        });

        return stepsPane;
    }
}

