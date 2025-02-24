package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_1 {
    private SignatureChecks_1() {}

    void checkFormatterFr() {
        v02 = ch.epfl.rechor.FormatterFr.formatDuration(v01);
        v02 = ch.epfl.rechor.FormatterFr.formatLeg(v03);
        v02 = ch.epfl.rechor.FormatterFr.formatLeg(v04);
        v02 = ch.epfl.rechor.FormatterFr.formatPlatformName(v05);
        v02 = ch.epfl.rechor.FormatterFr.formatRouteDestination(v03);
        v02 = ch.epfl.rechor.FormatterFr.formatTime(v06);
    }

    void checkPreconditions() {
        ch.epfl.rechor.Preconditions.checkArgument(v07);
    }

    void checkJourney() {
        v09 = (java.lang.Record) v08;
        v08 = new ch.epfl.rechor.journey.Journey(v10);
        v05 = v08.arrStop();
        v06 = v08.arrTime();
        v05 = v08.depStop();
        v06 = v08.depTime();
        v01 = v08.duration();
        v07 = v08.equals(v09);
        v11 = v08.hashCode();
        v10 = v08.legs();
        v02 = v08.toString();
    }

    void checkJourney_Leg() {
        v05 = v12.arrStop();
        v06 = v12.arrTime();
        v05 = v12.depStop();
        v06 = v12.depTime();
        v01 = v12.duration();
        v13 = v12.intermediateStops();
    }

    void checkJourney_Leg_Foot() {
        v09 = (java.lang.Record) v04;
        v09 = (ch.epfl.rechor.journey.Journey.Leg) v04;
        v04 = new ch.epfl.rechor.journey.Journey.Leg.Foot(v05, v06, v05, v06);
        v05 = v04.arrStop();
        v06 = v04.arrTime();
        v05 = v04.depStop();
        v06 = v04.depTime();
        v07 = v04.equals(v09);
        v11 = v04.hashCode();
        v13 = v04.intermediateStops();
        v07 = v04.isTransfer();
        v02 = v04.toString();
    }

    void checkJourney_Leg_Transport() {
        v09 = (java.lang.Record) v03;
        v09 = (ch.epfl.rechor.journey.Journey.Leg) v03;
        v03 = new ch.epfl.rechor.journey.Journey.Leg.Transport(v05, v06, v05, v06, v13, v14, v02, v02);
        v05 = v03.arrStop();
        v06 = v03.arrTime();
        v05 = v03.depStop();
        v06 = v03.depTime();
        v02 = v03.destination();
        v07 = v03.equals(v09);
        v11 = v03.hashCode();
        v13 = v03.intermediateStops();
        v02 = v03.route();
        v02 = v03.toString();
        v14 = v03.vehicle();
    }

    void checkJourney_Leg_IntermediateStop() {
        v09 = (java.lang.Record) v15;
        v15 = new ch.epfl.rechor.journey.Journey.Leg.IntermediateStop(v05, v06, v06);
        v06 = v15.arrTime();
        v06 = v15.depTime();
        v07 = v15.equals(v09);
        v11 = v15.hashCode();
        v05 = v15.stop();
        v02 = v15.toString();
    }

    void checkStop() {
        v09 = (java.lang.Record) v05;
        v05 = new ch.epfl.rechor.journey.Stop(v02, v02, v16, v16);
        v07 = v05.equals(v09);
        v11 = v05.hashCode();
        v16 = v05.latitude();
        v16 = v05.longitude();
        v02 = v05.name();
        v02 = v05.platformName();
        v02 = v05.toString();
    }

    void checkVehicle() {
        v09 = (java.lang.Enum) v14;
        v14 = ch.epfl.rechor.journey.Vehicle.AERIAL_LIFT;
        v17 = ch.epfl.rechor.journey.Vehicle.ALL;
        v14 = ch.epfl.rechor.journey.Vehicle.BUS;
        v14 = ch.epfl.rechor.journey.Vehicle.FERRY;
        v14 = ch.epfl.rechor.journey.Vehicle.FUNICULAR;
        v14 = ch.epfl.rechor.journey.Vehicle.METRO;
        v14 = ch.epfl.rechor.journey.Vehicle.TRAIN;
        v14 = ch.epfl.rechor.journey.Vehicle.TRAM;
        v14 = ch.epfl.rechor.journey.Vehicle.valueOf(v02);
        v18 = ch.epfl.rechor.journey.Vehicle.values();
    }

    java.time.Duration v01;
    java.lang.String v02;
    ch.epfl.rechor.journey.Journey.Leg.Transport v03;
    ch.epfl.rechor.journey.Journey.Leg.Foot v04;
    ch.epfl.rechor.journey.Stop v05;
    java.time.LocalDateTime v06;
    boolean v07;
    ch.epfl.rechor.journey.Journey v08;
    java.lang.Object v09;
    java.util.List<ch.epfl.rechor.journey.Journey.Leg> v10;
    int v11;
    ch.epfl.rechor.journey.Journey.Leg v12;
    java.util.List<ch.epfl.rechor.journey.Journey.Leg.IntermediateStop> v13;
    ch.epfl.rechor.journey.Vehicle v14;
    ch.epfl.rechor.journey.Journey.Leg.IntermediateStop v15;
    double v16;
    java.util.List<ch.epfl.rechor.journey.Vehicle> v17;
    ch.epfl.rechor.journey.Vehicle[] v18;
}
