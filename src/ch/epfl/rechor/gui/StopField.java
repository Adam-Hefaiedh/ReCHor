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

public record StopField(TextField textField, ObservableValue<String> stop0) {

    public static StopField create (StopIndex stopIndex){

        Popup resultsWindow = new Popup();
        resultsWindow.setHideOnEscape(false);

        ListView<String> resultsList = new ListView<>();
        resultsList.setFocusTraversable(false);
        resultsList.setMaxHeight(240);

        resultsWindow.getContent().add(resultsList);

        SimpleStringProperty selectedName = new SimpleStringProperty("");

        javafx.scene.control.TextField textField = new javafx.scene.control.TextField();


        textField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            int index = resultsList.getSelectionModel().getSelectedIndex();
            switch (event.getCode()) {
                case UP -> {
                    if (index > 0) {
                        resultsList.getSelectionModel().select(index - 1);
                        resultsList.scrollTo(index - 1);
                        event.consume();
                    }
                }
                case DOWN -> {
                    if (index < resultsList.getItems().size()-1) {
                        resultsList.getSelectionModel().select(index + 1);
                        resultsList.scrollTo(index + 1);
                        event.consume();
                    }
                }
            }
        });

        textField.focusedProperty().addListener((obsFocus, oldFocus, newFocus) -> {

            if (newFocus) {
                textField.textProperty().addListener((obsQuery, oldQuery, newQuery) -> {
                    if (!newQuery.equals(oldQuery)){
                        var stopsMatching = stopIndex.stopsMatching(newQuery, 30);
                        resultsList.setItems(FXCollections.observableArrayList(stopsMatching));
                        resultsList.getSelectionModel().selectFirst();
                    }
                });

                textField.boundsInLocalProperty().addListener((observable, oldBounds, newBounds) ->{
                    if (newBounds != oldBounds) {
                        Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                        resultsWindow.setAnchorX(bounds.getMinX());
                        resultsWindow.setAnchorY(bounds.getMaxY());
                        resultsWindow.show(textField.getScene().getWindow());
                    }
            });

            } else {
                textField.boundsInLocalProperty().addListener((observable, oldValue, newValue) ->{
                    resultsWindow.hide();
                });
                resultsWindow.hide();

                String selectedStop = resultsList.getSelectionModel().getSelectedItem();
                if (selectedStop != null) {
                    textField.setText(selectedStop);
                    selectedName.set(selectedStop);
                }
                else {
                    textField.setText("");
                    selectedName.set("");
                }
            }
        });
        return new StopField(textField, selectedName);
    }

    public void setTo (String stopName){
        textField.setText(stopName);
        ((SimpleStringProperty) stop0).set(stopName);
    }


}
