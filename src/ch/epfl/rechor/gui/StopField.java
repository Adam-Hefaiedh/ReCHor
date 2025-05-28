package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;

/**
 * StopField représente un champ de texte permettant à l'utilisateur
 * de rechercher un arrêt parmi ceux fournis par un StopIndex, avec
 * affichage dynamique des suggestions dans une liste déroulante.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public record StopField(TextField textField, ObservableValue<String> stop0) {

    private static final int MAX_REQUESTS = 30;

    /**
     * Crée un StopField connecté à un index d'arrêts donné.
     * Ce champ propose des suggestions filtrées en temps réel
     * et permet la sélection d'un nom d'arrêt.
     *
     * @param stopIndex l’index contenant les noms et alias d’arrêts
     * @return un StopField interactif prêt à être inséré dans une scène JavaFX
     */
    public static StopField create(StopIndex stopIndex) {

        TextField textField = new TextField();

        ListView<String> resultList = new ListView<>();
        resultList.setFocusTraversable(false);
        resultList.setMaxHeight(240);

        Popup popup = new Popup();
        popup.setAutoHide(false);
        popup.setHideOnEscape(true);
        popup.getContent().add(resultList);

        SimpleStringProperty selectedName = new SimpleStringProperty("");

        textField.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            int i = resultList.getSelectionModel().getSelectedIndex();
            switch (e.getCode()) {
                case UP -> {
                    if (i > 0) {
                        resultList.getSelectionModel().select(i - 1);
                        resultList.scrollTo(i - 1);
                        e.consume();
                    }
                }
                case DOWN -> {
                    if (i < resultList.getItems().size() - 1) {
                        resultList.getSelectionModel().select(i + 1);
                        resultList.scrollTo(i + 1);
                        e.consume();
                    }
                }
                case TAB -> {
                    popup.hide();
                    String selected = resultList.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        textField.setText(selected);
                        selectedName.set(selected);
                    } else {
                        textField.setText("");
                        selectedName.set("");
                    }
                }
            }

        });


        textField.textProperty().subscribe((newVal) -> {
            var matches = stopIndex.stopsMatching(newVal, MAX_REQUESTS);
            resultList.setItems(FXCollections.observableArrayList(matches));
            resultList.getSelectionModel().selectFirst();
        });


        Runnable positionPopup = () -> {
            Bounds b = textField.localToScreen(textField.getBoundsInLocal());
            if(b != null){
            popup.setAnchorX(b.getMinX());
            popup.setAnchorY(b.getMaxY());
            }
        };

        textField.boundsInLocalProperty().subscribe((newB) -> positionPopup.run());


        textField.focusedProperty().addListener((obs, oldFocus , isFocused) -> {
            if (isFocused) {
                positionPopup.run();
                popup.show(textField.getScene().getWindow());
            }
        });
        return new StopField(textField, selectedName);
    }

    /**
     * Définit manuellement la valeur du champ et met à jour la propriété observable.
     *
     * @param stopName le nom d’arrêt à définir
     */
    public void setTo(String stopName) {
        textField.setText(stopName);
        ((SimpleStringProperty) stop0).set(stopName);
    }
}