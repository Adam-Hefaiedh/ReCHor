package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Trips;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * La classe permet d'accéder à une table de courses représentée de manière aplatie.
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public final class BufferedTrips implements Trips {

    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;
    private final static int ROUTE_ID_Index = 0;
    private final static int DESTINATION_ID_Index = 1;

    /**
     * Construit une instance donnant accès aux données aplaties.
     * @param stringTable Chaine de caractère contenant les donnees du tableau.
     * @param buffer Tableau de byte contenant les index/donnees du tableau.
     */
    public BufferedTrips(List<String> stringTable, ByteBuffer buffer){
    Structure structure = new Structure(new Structure.Field(ROUTE_ID_Index, Structure.FieldType.U16),
            new Structure.Field(DESTINATION_ID_Index, Structure.FieldType.U16));
    structuredBuffer = new StructuredBuffer(structure, buffer);
    this.stringTable = stringTable;

    }

    @Override
    public int routeId(int id) {
        return structuredBuffer.getU16(ROUTE_ID_Index,id);
    }

    @Override
    public String destination(int id) {
        return stringTable.get(structuredBuffer.getU16(DESTINATION_ID_Index,id));
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
