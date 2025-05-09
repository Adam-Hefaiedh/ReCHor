package ch.epfl.rechor.gui;

import ch.epfl.rechor.journey.Vehicle;

import java.util.EnumMap;
import java.util.Map;
import javafx.scene.image.Image;

/**
 * La classe donne accès aux images (icônes) représentant les différents véhicules.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class VehicleIcons {
    private static final Map<Vehicle, Image> myMap = new EnumMap<>(Vehicle.class);

    private VehicleIcons(){}

    /**
     * Fournit l’icône associée à un véhicule donné.
     * @param vehicle véhicule dont on désire avoir l'icône
     * @return L'icône du véhicule donné
     */
    public static Image IconFor(Vehicle vehicle){
        return myMap.computeIfAbsent(vehicle, k-> new Image(k.name() +".png"));
    }
}
