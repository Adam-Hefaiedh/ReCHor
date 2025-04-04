package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static java.lang.Math.scalb;

/**
 * La classe permet d'accéder à une table de gares représentée de manière aplatie.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public final class BufferedStations implements Stations {

    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;
    private final static int NAME_ID = 0;
    private final static int LON = 1;
    private final static int LAT = 2;
    private final static double CONVERSION = scalb(360,-32);

    /**
     * Construit une instance donnant accès aux données aplaties.
     * @param stringTable Chaine de caractere contenant les donnees du tableau.
     * @param buffer Tableau de byte contenant les index des donnees du tableau.
     */
    public BufferedStations(List<String> stringTable, ByteBuffer buffer){
        Structure structure = new Structure(field(NAME_ID, Structure.FieldType.U16),
                field(LON, Structure.FieldType.S32),
                field(LAT, Structure.FieldType.S32));
       structuredBuffer = new StructuredBuffer(structure, buffer);
       this.stringTable = stringTable;
    }

    /**
     * Retourne le nom de la gare correspondant à l'index donné.
     * @param id l'index de la gare.
     * @return le nom de la gare correspondant à l'index donné.
     * @throws IndexOutOfBoundsException si l'index id est invalide.
     */
    @Override
    public String name(int id) {
        return stringTable.get(structuredBuffer.getU16(NAME_ID,id));
    }

    /**
     * Retourne la longitude, en degrés, de la gare correspondant à l'index donné.
     * @param id l'index de la gare.
     * @return la longitude de la gare.
     * @throws IndexOutOfBoundsException si l'index id est invalide.
     */
    @Override
    public double longitude(int id) {
        int longitude = structuredBuffer.getS32(LON,id);
        return longitude*CONVERSION;
    }

    /**
     * Retourne la latitude, en degrés, de la gare correspondant à l'index donné.
     * @param id l'index de la gare.
     * @return la latitude de la gare.
     * @throws IndexOutOfBoundsException si l'index id est invalide.
     */
    @Override
    public double latitude(int id) {
        int latitude = structuredBuffer.getS32(LAT,id);
        return latitude*CONVERSION;
    }

    /**
     * Retourne la taille de tableau de donnee lier à la station.
     * @return la taille de tableau de donnee lier à la station.
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
