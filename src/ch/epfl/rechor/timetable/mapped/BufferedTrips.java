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

    /**
     * Retourne l'index de la ligne (route) à laquelle appartient la course d'index donné.
     *
     * @param id l'index de la course.
     * @return l'index de la ligne associée à la course.
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à zero ou supérieur ou égal à size()).
     */
    @Override
    public int routeId(int id) {
        return structuredBuffer.getU16(ROUTE_ID_Index,id);
    }

    /**
     * Retourne le nom de la destination finale de la course d'index donné.
     *
     * @param id l'index de la course.
     * @return le nom de la destination finale de la course.
     * @throws IndexOutOfBoundsException si l'index id est invalide (inférieur à zero ou supérieur ou égal à size()).
     */
    @Override
    public String destination(int id) {
        return stringTable.get(structuredBuffer.getU16(DESTINATION_ID_Index,id));
    }

    /**
     * Retourne la taille des donnees lier aux courses.
     * @return la taille des donnees lier aux courses.
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
