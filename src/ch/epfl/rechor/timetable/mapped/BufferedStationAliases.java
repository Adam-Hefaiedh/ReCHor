package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.sql.SQLOutput;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * La classe permet d'accéder à une table de noms alternatifs de gares représentée de manière aplatie
 */
public final class BufferedStationAliases implements StationAliases {
    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;
    private final static int ALIAS_ID = 0;
    private final static int STATION_NAME_ID = 1;

    /**
     * Construit une instance donnant accès aux données aplaties
     * @param stringTable
     * Chaine de caractere contenant les donnees du tableau
     * @param buffer
     * Tableau de byte contenant les index des donnees du tableau
     */
    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer){
        Structure structure = new Structure(field(ALIAS_ID, Structure.FieldType.U16),
                field(STATION_NAME_ID, Structure.FieldType.U16));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        this.stringTable = stringTable;
    }

    /**
     * Retourne le nom alternatif de la gare pour l'index donné.
     * @param id
     * l'index du nom alternatif
     * @return le nom alternatif de la gare pour l'index donné.
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    @Override
    public String alias(int id) {
        return stringTable.get(structuredBuffer.getU16(ALIAS_ID,id));
    }

    /**
     * Retourne le nom officiel de la gare correspondant à l'alias d'index donné.
     * @param id
     * l'index du nom alternatif
     * @return le nom officiel de la gare correspondant à l'alias d'index donné.
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    @Override
    public String stationName(int id) {
        return stringTable.get(structuredBuffer.getU16(STATION_NAME_ID,id));
    }

    /**
     * Retourne la taille de tableau de donnee lier au alias de station
     * @return la taille de tableau de donnee lier au alias de station
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
