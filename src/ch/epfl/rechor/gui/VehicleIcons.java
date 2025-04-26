package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Vehicle;

import java.util.EnumMap;
import java.util.Map;
import javafx.scene.image.Image;

public class VehicleIcons {
    Map<Vehicle, Image> myMap = new EnumMap<>(Vehicle.class);

    private VehicleIcons(){}

    public Image IconFor(Vehicle vehicle){
        return myMap.computeIfAbsent(vehicle, k-> new Image(k.name() +".png"));
    }
}
