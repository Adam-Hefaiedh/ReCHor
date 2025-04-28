package ch.epfl.rechor.gui;


import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record DetailUI (Node rootNode) {

    public static DetailUI create(ObservableValue<Journey> journey) {

        Label noneLabel = new Label("Aucun voyage");
        VBox vBox1 = new VBox(noneLabel);
        vBox1.setId("no-journey");
        StackPane nonePane = new StackPane(noneLabel);
        nonePane.setAlignment(Pos.CENTER);

        GridPane stepsPane = createStepsPane(journey);
        stepsPane.setId("legs");
        stepsPane.setVisible(false);

        Button map = new Button("Carte");
        Button calendar = new Button("Calendrier");
        HBox buttonBar = new HBox(2, map, calendar);
        buttonBar.setId("buttons");
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        VBox vbox2 = new VBox(5, buttonBar, stepsPane);

        BorderPane root = new BorderPane();
        root.setCenter(nonePane);
        root.setBottom(buttonBar);


        journey.addListener((observableValue, oldJourney, newJourney) -> {
            if (newJourney == null) root.setCenter(nonePane);
            else { stepsPane.setVisible(true);
                root.setCenter(stepsPane);}
        });
        return new DetailUI(root);
    }

    private static GridPane createStepsPane(ObservableValue<Journey> journey) {

        GridPane stepsPane = new GridPane();
        List<Journey.Leg> steps = journey.getValue().legs();

        AtomicInteger rowIndex= new AtomicInteger(0);
        steps.forEach((l) -> {

            if (l instanceof Journey.Leg.Foot){
                Text transfer = new Text(FormatterFr.formatLeg(l));//je comprends pas,pourquoi ca ne prend pas une instance de foot?
                stepsPane.add(transfer, 2, rowIndex.get(), 2, 1);
                GridPane.setHalignment(transfer, HPos.LEFT);
                rowIndex.getAndIncrement();
            }

            else if(l instanceof Journey.Leg.Transport) {

                //ligne 1
                Text depTime = new Text(FormatterFr.formatTime(l.depTime()));
                Circle circle = new Circle(3);
                Text depStop = new Text(l.depStop().toString());
                Text depPlatform; //je n'arrive pas a trouver le nom du quai
                stepsPane.add(depTime,      0, rowIndex.get()); GridPane.setHalignment(depTime, HPos.RIGHT);
                stepsPane.add(circle,           1, rowIndex.get()); GridPane.setHalignment(circle, HPos.CENTER);
                stepsPane.add(depStop,   2, rowIndex.get()); GridPane.setHalignment(depStop, HPos.LEFT);
                stepsPane.add(depPlatform,  3, rowIndex.get()); GridPane.setHalignment(depPlatform, HPos.LEFT);
                rowIndex.getAndIncrement();

                //ligne 2
                ImageView vehicleImage = new ImageView(((Journey.Leg.Transport) l).vehicle().toString() + ".png");
                vehicleImage.setFitWidth(31);
                vehicleImage.setFitHeight(31);
                vehicleImage.setPreserveRatio(true);
                Text routeDest = new Text(FormatterFr.formatRouteDestination(l));
                stepsPane.add(vehicleImage, 0, rowIndex.get(), 1, 2);
                GridPane.setValignment(vehicleImage, VPos.CENTER);
                stepsPane.add(routeDest, 2, rowIndex.get(), 2, 1);
                GridPane.setHalignment(routeDest, HPos.LEFT);
                rowIndex.getAndIncrement();

                //ligne 3
                if (!l.intermediateStops().isEmpty()) {
                    Accordion interStops = new Accordion();
                    TitledPane titledPane = new TitledPane();
                    titledPane.setText(l.intermediateStops().size() + " arrêts, " + FormatterFr.formatDuration(l.duration()));

                    GridPane interStopsGrid = new GridPane();
                    interStopsGrid.setHgap(5);
                    interStopsGrid.setVgap(2);
                    AtomicInteger rowIndex2 = new AtomicInteger();
                    l.intermediateStops().forEach((i) -> {
                        interStopsGrid.add(new Text(FormatterFr.formatTime(i.depTime())), 0, rowIndex2.get());
                        interStopsGrid.add(new Text(FormatterFr.formatTime(i.arrTime())), 1, rowIndex2.get());
                        interStopsGrid.add(new Text(i.stop().toString()), 2, rowIndex2.get());
                    rowIndex2.getAndIncrement();
                    });

                    titledPane.setContent(interStopsGrid);
                    interStops.getPanes().add(titledPane);
                    stepsPane.add(interStops, 2, rowIndex.get(), 2, 1);
                    rowIndex.getAndIncrement();
                }


                //ligne 4
                Text arrTime = new Text(FormatterFr.formatTime(l.arrTime()));
                Circle circle1 = new Circle(3);
                Text arrStop = new Text(l.arrStop().toString());
                Text arrPlatform; // je ne sais pas comment l'avoir
                stepsPane.add(arrTime,      0, rowIndex.get()); GridPane.setHalignment(depTime, HPos.RIGHT);
                stepsPane.add(circle1,           1, rowIndex.get()); GridPane.setHalignment(circle, HPos.CENTER);
                stepsPane.add(arrStop,   2, rowIndex.get()); GridPane.setHalignment(depStop, HPos.LEFT);
                stepsPane.add(arrPlatform,  3, rowIndex.get()); GridPane.setHalignment(depPlatform, HPos.LEFT);
            }
        });
        return stepsPane;
    }
}

