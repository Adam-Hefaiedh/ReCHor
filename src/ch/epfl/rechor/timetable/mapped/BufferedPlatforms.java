package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static java.lang.Integer.parseInt;

/**
 * La classe permet d'accéder à une table de voies ou quais représentée de manière aplatie
 */
public final class BufferedPlatforms implements Platforms {
    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;
    private final static int NAME_ID = 0;
    private final static int STATION_ID = 1;

    /**
     * Construit une instance donnant accès aux données aplaties
     * @param stringTable
     * Chaine de caractere contenant les donnees du tableau
     * @param buffer
     * Tableau de byte contenant les index des donnees du tableau
     */
    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer){
        Structure structure = new Structure(field(NAME_ID, Structure.FieldType.U16),
                field(STATION_ID, Structure.FieldType.U16));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        this.stringTable = stringTable;
    }

    /**
     * Retourne le nom de la voie/quai
     * @param id
     * l'index de la voie ou du quai
     * @return le nom de la voie/quai
     * @throws IndexOutOfBoundsException si l'index est invalide
     */
    @Override
    public String name(int id) {
        return stringTable.get(structuredBuffer.getU16(NAME_ID,id));
    }

    /**
     * Retourne l'index de la station lier a la platform
     * @param id
     * l'index de la voie ou du quai
     * @return l'index de la station lier a la platform
     * @throws IndexOutOfBoundsException si l'index est invalide
     */
    @Override
    public int stationId(int id) {
        return structuredBuffer.getU16(STATION_ID,id);
    }

    /**
     * Retourne la taille des donnees lier a platform
     * @return la taille des donnees lier a platform
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
