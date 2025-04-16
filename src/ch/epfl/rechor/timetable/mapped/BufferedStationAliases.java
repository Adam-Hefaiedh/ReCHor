package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * La classe permet d'accéder à une table de noms alternatifs de gares représentée de manière aplatie.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public final class BufferedStationAliases implements StationAliases {
    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;
    private final static int ALIAS_ID = 0;
    private final static int STATION_NAME_ID = 1;

    /**
     * Construit une instance donnant accès aux données aplaties.
     * @param stringTable Chaine de caractère contenant les donnees du tableau.
     * @param buffer Tableau de byte contenant les index des donnees du tableau.
     */
    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer){
        Structure structure = new Structure(field(ALIAS_ID, Structure.FieldType.U16),
                field(STATION_NAME_ID, Structure.FieldType.U16));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        this.stringTable = stringTable;
    }

    @Override
    public String alias(int id) {
        return stringTable.get(structuredBuffer.getU16(ALIAS_ID,id));
    }

    @Override
    public String stationName(int id) {
        return stringTable.get(structuredBuffer.getU16(STATION_NAME_ID,id));
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
