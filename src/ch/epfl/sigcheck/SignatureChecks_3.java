package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_3 {
    private SignatureChecks_3() {}

    void checkConnections() {
        v02 = (ch.epfl.rechor.timetable.Indexed) v01;
        v03 = v01.arrMins(v03);
        v03 = v01.arrStopId(v03);
        v03 = v01.depMins(v03);
        v03 = v01.depStopId(v03);
        v03 = v01.nextConnectionId(v03);
        v03 = v01.tripId(v03);
        v03 = v01.tripPos(v03);
    }

    void checkIndexed() {
        v03 = v04.size();
    }

    void checkPlatforms() {
        v02 = (ch.epfl.rechor.timetable.Indexed) v05;
        v06 = v05.name(v03);
        v03 = v05.stationId(v03);
    }

    void checkRoutes() {
        v02 = (ch.epfl.rechor.timetable.Indexed) v07;
        v06 = v07.name(v03);
        v08 = v07.vehicle(v03);
    }

    void checkStationAliases() {
        v02 = (ch.epfl.rechor.timetable.Indexed) v09;
        v06 = v09.alias(v03);
        v06 = v09.stationName(v03);
    }

    void checkStations() {
        v02 = (ch.epfl.rechor.timetable.Indexed) v10;
        v11 = v10.latitude(v03);
        v11 = v10.longitude(v03);
        v06 = v10.name(v03);
    }

    void checkTimeTable() {
        v01 = v12.connectionsFor(v13);
        v14 = v12.isPlatformId(v03);
        v14 = v12.isStationId(v03);
        v06 = v12.platformName(v03);
        v05 = v12.platforms();
        v07 = v12.routes();
        v09 = v12.stationAliases();
        v03 = v12.stationId(v03);
        v10 = v12.stations();
        v15 = v12.transfers();
        v16 = v12.tripsFor(v13);
    }

    void checkTransfers() {
        v02 = (ch.epfl.rechor.timetable.Indexed) v15;
        v03 = v15.arrivingAt(v03);
        v03 = v15.depStationId(v03);
        v03 = v15.minutes(v03);
        v03 = v15.minutesBetween(v03, v03);
    }

    void checkTrips() {
        v02 = (ch.epfl.rechor.timetable.Indexed) v16;
        v06 = v16.destination(v03);
        v03 = v16.routeId(v03);
    }

    void checkParetoFront() {
        v17 = ch.epfl.rechor.journey.ParetoFront.EMPTY;
        v17.forEach(v18);
        v19 = v17.get(v03, v03);
        v03 = v17.size();
        v06 = v17.toString();
    }

    void checkParetoFront_Builder() {
        v20 = new ch.epfl.rechor.journey.ParetoFront.Builder(v20);
        v20 = new ch.epfl.rechor.journey.ParetoFront.Builder();
        v20 = v20.add(v19);
        v20 = v20.add(v03, v03, v03);
        v20 = v20.addAll(v20);
        v17 = v20.build();
        v20 = v20.clear();
        v20.forEach(v18);
        v14 = v20.fullyDominates(v20, v03);
        v14 = v20.isEmpty();
        v06 = v20.toString();
    }

    ch.epfl.rechor.timetable.Connections v01;
    java.lang.Object v02;
    int v03;
    ch.epfl.rechor.timetable.Indexed v04;
    ch.epfl.rechor.timetable.Platforms v05;
    java.lang.String v06;
    ch.epfl.rechor.timetable.Routes v07;
    ch.epfl.rechor.journey.Vehicle v08;
    ch.epfl.rechor.timetable.StationAliases v09;
    ch.epfl.rechor.timetable.Stations v10;
    double v11;
    ch.epfl.rechor.timetable.TimeTable v12;
    java.time.LocalDate v13;
    boolean v14;
    ch.epfl.rechor.timetable.Transfers v15;
    ch.epfl.rechor.timetable.Trips v16;
    ch.epfl.rechor.journey.ParetoFront v17;
    java.util.function.LongConsumer v18;
    long v19;
    ch.epfl.rechor.journey.ParetoFront.Builder v20;
}
