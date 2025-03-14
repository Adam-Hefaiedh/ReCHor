package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_4 {
    private SignatureChecks_4() {}

    void checkStructure() {
        v01 = new ch.epfl.rechor.timetable.mapped.Structure(v02);
        v05 = ch.epfl.rechor.timetable.mapped.Structure.field(v03, v04);
        v03 = v01.offset(v03, v03);
        v03 = v01.totalSize();
    }

    void checkStructure_Field() {
        v06 = (Record) v05;
        v05 = new ch.epfl.rechor.timetable.mapped.Structure.Field(v03, v04);
        v07 = v05.equals(v06);
        v03 = v05.hashCode();
        v03 = v05.index();
        v08 = v05.toString();
        v04 = v05.type();
    }

    void checkStructure_FieldType() {
        v06 = (Enum) v04;
        v04 = ch.epfl.rechor.timetable.mapped.Structure.FieldType.S32;
        v04 = ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
        v04 = ch.epfl.rechor.timetable.mapped.Structure.FieldType.U8;
        v04 = ch.epfl.rechor.timetable.mapped.Structure.FieldType.valueOf(v08);
        v09 = ch.epfl.rechor.timetable.mapped.Structure.FieldType.values();
    }

    void checkStructuredBuffer() {
        v10 = new ch.epfl.rechor.timetable.mapped.StructuredBuffer(v01, v11);
        v03 = v10.getS32(v03, v03);
        v03 = v10.getU16(v03, v03);
        v03 = v10.getU8(v03, v03);
        v03 = v10.size();
    }

    void checkBufferedStations() {
        v06 = (ch.epfl.rechor.timetable.Stations) v12;
        v12 = new ch.epfl.rechor.timetable.mapped.BufferedStations(v13, v11);
        v14 = v12.latitude(v03);
        v14 = v12.longitude(v03);
        v08 = v12.name(v03);
        v03 = v12.size();
    }

    void checkBufferedStationAliases() {
        v06 = (ch.epfl.rechor.timetable.StationAliases) v15;
        v15 = new ch.epfl.rechor.timetable.mapped.BufferedStationAliases(v13, v11);
        v08 = v15.alias(v03);
        v03 = v15.size();
        v08 = v15.stationName(v03);
    }

    void checkBufferedPlatforms() {
        v06 = (ch.epfl.rechor.timetable.Platforms) v16;
        v16 = new ch.epfl.rechor.timetable.mapped.BufferedPlatforms(v13, v11);
        v08 = v16.name(v03);
        v03 = v16.size();
        v03 = v16.stationId(v03);
    }

    ch.epfl.rechor.timetable.mapped.Structure v01;
    ch.epfl.rechor.timetable.mapped.Structure.Field[] v02;
    int v03;
    ch.epfl.rechor.timetable.mapped.Structure.FieldType v04;
    ch.epfl.rechor.timetable.mapped.Structure.Field v05;
    Object v06;
    boolean v07;
    String v08;
    ch.epfl.rechor.timetable.mapped.Structure.FieldType[] v09;
    ch.epfl.rechor.timetable.mapped.StructuredBuffer v10;
    java.nio.ByteBuffer v11;
    ch.epfl.rechor.timetable.mapped.BufferedStations v12;
    java.util.List<String> v13;
    double v14;
    ch.epfl.rechor.timetable.mapped.BufferedStationAliases v15;
    ch.epfl.rechor.timetable.mapped.BufferedPlatforms v16;
}
