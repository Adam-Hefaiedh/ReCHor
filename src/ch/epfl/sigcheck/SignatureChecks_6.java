package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_6 {
    private SignatureChecks_6() {}

    void checkFileTimeTable() {
        v02 = (Record) v01;
        v02 = (ch.epfl.rechor.timetable.TimeTable) v01;
        v01 = new ch.epfl.rechor.timetable.mapped.FileTimeTable(v03, v04, v05, v06, v07, v08, v09);
        try {
            v10 = ch.epfl.rechor.timetable.mapped.FileTimeTable.in(v03);
        } catch (java.io.IOException e) {}
        v12 = v01.connectionsFor(v11);
        v03 = v01.directory();
        v13 = v01.equals(v02);
        v14 = v01.hashCode();
        v07 = v01.platforms();
        v08 = v01.routes();
        v06 = v01.stationAliases();
        v05 = v01.stations();
        v04 = v01.stringTable();
        v15 = v01.toString();
        v09 = v01.transfers();
        v16 = v01.tripsFor(v11);
    }

    void checkProfile() {
        v02 = (Record) v17;
        v17 = new ch.epfl.rechor.journey.Profile(v10, v11, v14, v18);
        v14 = v17.arrStationId();
        v12 = v17.connections();
        v11 = v17.date();
        v13 = v17.equals(v02);
        v19 = v17.forStation(v14);
        v14 = v17.hashCode();
        v18 = v17.stationFront();
        v10 = v17.timeTable();
        v15 = v17.toString();
        v16 = v17.trips();
    }

    void checkProfile_Builder() {
        v20 = new ch.epfl.rechor.journey.Profile.Builder(v10, v11, v14);
        v17 = v20.build();
        v21 = v20.forStation(v14);
        v21 = v20.forTrip(v14);
        v20.setForStation(v14, v21);
        v20.setForTrip(v14, v21);
    }

    void checkJourneyExtractor() {
        v22 = ch.epfl.rechor.journey.JourneyExtractor.journeys(v17, v14);
    }

    ch.epfl.rechor.timetable.mapped.FileTimeTable v01;
    Object v02;
    java.nio.file.Path v03;
    java.util.List<String> v04;
    ch.epfl.rechor.timetable.Stations v05;
    ch.epfl.rechor.timetable.StationAliases v06;
    ch.epfl.rechor.timetable.Platforms v07;
    ch.epfl.rechor.timetable.Routes v08;
    ch.epfl.rechor.timetable.Transfers v09;
    ch.epfl.rechor.timetable.TimeTable v10;
    java.time.LocalDate v11;
    ch.epfl.rechor.timetable.Connections v12;
    boolean v13;
    int v14;
    String v15;
    ch.epfl.rechor.timetable.Trips v16;
    ch.epfl.rechor.journey.Profile v17;
    java.util.List<ch.epfl.rechor.journey.ParetoFront> v18;
    ch.epfl.rechor.journey.ParetoFront v19;
    ch.epfl.rechor.journey.Profile.Builder v20;
    ch.epfl.rechor.journey.ParetoFront.Builder v21;
    java.util.List<ch.epfl.rechor.journey.Journey> v22;
}
