package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Connections;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static ch.epfl.rechor.Bits32_24_8.*;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * La classe permet d'accéder à une table de liaisons représentée de manière aplatie
 */
public final class BufferedConnections implements Connections {
    private final StructuredBuffer structuredBuffer;
    private final IntBuffer buffer;
    private final static int DEP_STOP_ID = 0;
    private final static int DEP_MINUTES = 1;
    private final static int ARR_STOP_ID = 2;
    private final static int ARR_MINUTES = 3;
    private final static int TRIP_POS_ID = 4;

    /**
     * Construit une instance donnant accès aux données aplaties
     * @param succBuffer
     * Tableau de byte contenant les connections suivantes de chaque connection.
     * @param buffer
     * Tableau de byte contenant les index/donnees du tableau
     */
    public BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer){
        Structure structure = new Structure(
                field(DEP_STOP_ID, Structure.FieldType.U16),
                field(DEP_MINUTES, Structure.FieldType.U16),
                field(ARR_STOP_ID, Structure.FieldType.U16),
                field(ARR_MINUTES, Structure.FieldType.U16),
                field(TRIP_POS_ID, Structure.FieldType.S32));
        structuredBuffer = new StructuredBuffer(structure, buffer);
        this.buffer = succBuffer.asIntBuffer();
    }

    /**
     * Retourne l'index de l'arrêt de départ de la liaison d'index donné.
     *
     * @param id l'index de la liaison
     * @return l'index de l'arrêt de départ
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    @Override
    public int depStopId(int id) {
        return structuredBuffer.getU16(DEP_STOP_ID,id);
    }

    /**
     * Retourne l'heure de départ de la liaison d'index donné, exprimée en minutes après minuit.
     *
     * @param id l'index de la liaison
     * @return l'heure de départ en minutes après minuit
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    @Override
    public int depMins(int id) {
        return structuredBuffer.getU16(DEP_MINUTES,id);
    }

    /**
     * Retourne l'index de l'arrêt d'arrivée de la liaison d'index donné.
     *
     * @param id l'index de la liaison
     * @return l'index de l'arrêt d'arrivée
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    @Override
    public int arrStopId(int id) {
        return structuredBuffer.getU16(ARR_STOP_ID,id);
    }

    /**
     * Retourne l'heure d'arrivée de la liaison d'index donné, exprimée en minutes après minuit.
     *
     * @param id l'index de la liaison
     * @return l'heure d'arrivée en minutes après minuit
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    @Override
    public int arrMins(int id) {
        return structuredBuffer.getU16(ARR_MINUTES,id);
    }

    /**
     * Retourne l'index de la course à laquelle appartient la liaison d'index donné.
     *
     * @param id l'index de la liaison
     * @return l'index de la course associée
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    @Override
    public int tripId(int id) {
        return unpack24(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    /**
     * Retourne la position de la liaison d'index donné dans la course à laquelle elle appartient.
     * La première liaison d'une course à l'index 0.
     *
     * @param id l'index de la liaison
     * @return la position de la liaison dans la course
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    @Override
    public int tripPos(int id) {
        return unpack8(structuredBuffer.getS32(TRIP_POS_ID,id));
    }


    /**
     * Retourne l'index de la liaison suivant celle d'index donné dans la course à laquelle elle appartient.
     * Si la liaison d'index donné est la dernière de la course, cette méthode retourne l'index de la première liaison de la course.
     *
     * @param id l'index de la liaison
     * @return l'index de la liaison suivante ou, si la liaison est la dernière, l'index de la première liaison de la course
     * @throws IndexOutOfBoundsException si l'index id est invalide
     */
    @Override
    public int nextConnectionId(int id) {
        return buffer.get(id);
    }

    /**
     * Retourne la taille des donnees lier aux liaisons
     * @return la taille des donnees lier aux liaisons
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
