package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.converter.LocalTimeStringConverter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * La classe représente l'interface de requête
 * @param rootNode nœud JavaFX à la racine de son graphe de scène
 * @param depStopO valeur observable contenant le nom de l'arrêt de départ
 * @param arrStopO valeur observable contenant le nom de l'arrêt d'arrivée
 * @param dateO valeur observable contenant la date de voyage
 * @param timeO valeur observable contenant l'heure de voyage
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public record QueryUI(Node rootNode,
                      ObservableValue<String> depStopO,
                      ObservableValue<String> arrStopO,
                      ObservableValue<LocalDate> dateO,
                      ObservableValue<LocalTime> timeO) {

    /**
     * Permet de créer le graphe de scène de l'interface de requête
     * @param stopIndex  index de nom d'arrêt qui est utilisé pour rechercher les noms d'arrêts
     * @return une instance de QueryUI contenant le nœud JavaFX qui se trouve à sa racine
     */
    public static QueryUI create(StopIndex stopIndex) {

        StopField depField = StopField.create(stopIndex);
        StopField arrField = StopField.create(stopIndex);

        depField.textField().setId("depStop");
        arrField.textField().setId("arrStop");

        depField.textField().setPromptText("Départ\u202f:");
        arrField.textField().setPromptText("Arrivée\u202f:");


        Button swapButton = new Button("⇄");
        swapButton.setId("swap-button");
        swapButton.setOnAction(e -> {
            String dep = depField.textField().getText();
            String arr = arrField.textField().getText();
            depField.setTo(arr);
            arrField.setTo(dep);
        });

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setId("date");

        DateTimeFormatter displayFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter parseFmt = DateTimeFormatter.ofPattern("[H:mm][HH:mm]");
        LocalTimeStringConverter converter = new LocalTimeStringConverter(displayFmt, parseFmt);

        TextFormatter<LocalTime> tf = new TextFormatter<>(converter, LocalTime.now());
        TextField timeField = new TextField();
        timeField.setId("time");
        timeField.setTextFormatter(tf);

        ObjectProperty<LocalDate> dateProp = datePicker.valueProperty();
        ObservableValue<LocalTime> timeProp = tf.valueProperty();

        HBox topRow = new HBox(10,
                new Label("Départ\u202f:"), depField.textField(),
                swapButton,
                new Label("Arrivée\u202f:"), arrField.textField()
        );


        HBox bottomRow = new HBox(10,
                new Label("Date\u202f:"), datePicker,
                new Label("Heure\u202f:"), timeField
        );

        VBox root = new VBox(10, topRow, bottomRow);
        root.getStylesheets().add(Objects.requireNonNull(QueryUI.class.getResource("/query.css")).toExternalForm());

        depField.stop0().subscribe(depField::setTo);
        arrField.stop0().subscribe(arrField::setTo);

        return new QueryUI(root,
                depField.stop0(),
                arrField.stop0(),
                dateProp,
                timeProp
        );
    }
}


