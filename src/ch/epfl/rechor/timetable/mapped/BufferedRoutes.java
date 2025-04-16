package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Routes;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * La classe permet d'accéder à une table de lignes représentée de manière aplatie.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public final class BufferedRoutes implements Routes {
    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;
    private final static int NAME_ID = 0;
    private final static int KIND_ID = 1;

    /**
     * Construit une instance donnant accès aux données aplaties.
     * @param stringTable Chaine de caractère contenant les donnees du tableau.
     * @param buffer Tableau de byte contenant les index des donnees du tableau.
     */
    public BufferedRoutes(List<String> stringTable, ByteBuffer buffer){
        Structure structure = new Structure(field(NAME_ID, Structure.FieldType.U16),
                field(KIND_ID, Structure.FieldType.U8));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        this.stringTable = stringTable;
    }

    @Override
    public Vehicle vehicle(int id) {
        return Vehicle.values()[structuredBuffer.getU8(KIND_ID,id)];
    }

    @Override
    public String name(int id) {
        return stringTable.get(structuredBuffer.getU16(NAME_ID,id));
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
