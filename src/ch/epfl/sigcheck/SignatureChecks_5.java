package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_5 {
    private SignatureChecks_5() {}

    void checkBufferedTrips() {
        v02 = (ch.epfl.rechor.timetable.Trips) v01;
        v01 = new ch.epfl.rechor.timetable.mapped.BufferedTrips(v03, v04);
        v06 = v01.destination(v05);
        v05 = v01.routeId(v05);
        v05 = v01.size();
    }

    void checkBufferedRoutes() {
        v02 = (ch.epfl.rechor.timetable.Routes) v07;
        v07 = new ch.epfl.rechor.timetable.mapped.BufferedRoutes(v03, v04);
        v06 = v07.name(v05);
        v05 = v07.size();
        v08 = v07.vehicle(v05);
    }

    void checkBufferedConnections() {
        v02 = (ch.epfl.rechor.timetable.Connections) v09;
        v09 = new ch.epfl.rechor.timetable.mapped.BufferedConnections(v04, v04);
        v05 = v09.arrMins(v05);
        v05 = v09.arrStopId(v05);
        v05 = v09.depMins(v05);
        v05 = v09.depStopId(v05);
        v05 = v09.nextConnectionId(v05);
        v05 = v09.size();
        v05 = v09.tripId(v05);
        v05 = v09.tripPos(v05);
    }

    void checkBufferedTransfers() {
        v02 = (ch.epfl.rechor.timetable.Transfers) v10;
        v10 = new ch.epfl.rechor.timetable.mapped.BufferedTransfers(v04);
        v05 = v10.arrivingAt(v05);
        v05 = v10.depStationId(v05);
        v05 = v10.minutes(v05);
        v05 = v10.minutesBetween(v05, v05);
        v05 = v10.size();
    }

    ch.epfl.rechor.timetable.mapped.BufferedTrips v01;
    Object v02;
    java.util.List<String> v03;
    java.nio.ByteBuffer v04;
    int v05;
    String v06;
    ch.epfl.rechor.timetable.mapped.BufferedRoutes v07;
    ch.epfl.rechor.journey.Vehicle v08;
    ch.epfl.rechor.timetable.mapped.BufferedConnections v09;
    ch.epfl.rechor.timetable.mapped.BufferedTransfers v10;
}
