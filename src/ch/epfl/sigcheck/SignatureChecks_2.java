package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_2 {
    private SignatureChecks_2() {}

    void checkIcalBuilder() {
        v01 = new ch.epfl.rechor.IcalBuilder();
        v01 = v01.add(v02, v03);
        v01 = v01.add(v02, v04);
        v01 = v01.begin(v05);
        v04 = v01.build();
        v01 = v01.end();
    }

    void checkIcalBuilder_Name() {
        v06 = (java.lang.Enum) v02;
        v02 = ch.epfl.rechor.IcalBuilder.Name.BEGIN;
        v02 = ch.epfl.rechor.IcalBuilder.Name.DESCRIPTION;
        v02 = ch.epfl.rechor.IcalBuilder.Name.DTEND;
        v02 = ch.epfl.rechor.IcalBuilder.Name.DTSTAMP;
        v02 = ch.epfl.rechor.IcalBuilder.Name.DTSTART;
        v02 = ch.epfl.rechor.IcalBuilder.Name.END;
        v02 = ch.epfl.rechor.IcalBuilder.Name.PRODID;
        v02 = ch.epfl.rechor.IcalBuilder.Name.SUMMARY;
        v02 = ch.epfl.rechor.IcalBuilder.Name.UID;
        v02 = ch.epfl.rechor.IcalBuilder.Name.VERSION;
        v02 = ch.epfl.rechor.IcalBuilder.Name.valueOf(v04);
        v07 = ch.epfl.rechor.IcalBuilder.Name.values();
    }

    void checkIcalBuilder_Component() {
        v06 = (java.lang.Enum) v05;
        v05 = ch.epfl.rechor.IcalBuilder.Component.VCALENDAR;
        v05 = ch.epfl.rechor.IcalBuilder.Component.VEVENT;
        v05 = ch.epfl.rechor.IcalBuilder.Component.valueOf(v04);
        v08 = ch.epfl.rechor.IcalBuilder.Component.values();
    }

    void checkJourneyIcalConverter() {
        v04 = ch.epfl.rechor.journey.JourneyIcalConverter.toIcalendar(v09);
    }

    void checkBits32_24_8() {
        v10 = ch.epfl.rechor.Bits32_24_8.pack(v10, v10);
        v10 = ch.epfl.rechor.Bits32_24_8.unpack24(v10);
        v10 = ch.epfl.rechor.Bits32_24_8.unpack8(v10);
    }

    void checkPackedRange() {
        v10 = ch.epfl.rechor.PackedRange.endExclusive(v10);
        v10 = ch.epfl.rechor.PackedRange.length(v10);
        v10 = ch.epfl.rechor.PackedRange.pack(v10, v10);
        v10 = ch.epfl.rechor.PackedRange.startInclusive(v10);
    }

    void checkPackedCriteria() {
        v10 = ch.epfl.rechor.journey.PackedCriteria.arrMins(v11);
        v10 = ch.epfl.rechor.journey.PackedCriteria.changes(v11);
        v10 = ch.epfl.rechor.journey.PackedCriteria.depMins(v11);
        v12 = ch.epfl.rechor.journey.PackedCriteria.dominatesOrIsEqual(v11, v11);
        v12 = ch.epfl.rechor.journey.PackedCriteria.hasDepMins(v11);
        v11 = ch.epfl.rechor.journey.PackedCriteria.pack(v10, v10, v10);
        v10 = ch.epfl.rechor.journey.PackedCriteria.payload(v11);
        v11 = ch.epfl.rechor.journey.PackedCriteria.withAdditionalChange(v11);
        v11 = ch.epfl.rechor.journey.PackedCriteria.withDepMins(v11, v10);
        v11 = ch.epfl.rechor.journey.PackedCriteria.withPayload(v11, v10);
        v11 = ch.epfl.rechor.journey.PackedCriteria.withoutDepMins(v11);
    }

    ch.epfl.rechor.IcalBuilder v01;
    ch.epfl.rechor.IcalBuilder.Name v02;
    java.time.LocalDateTime v03;
    java.lang.String v04;
    ch.epfl.rechor.IcalBuilder.Component v05;
    java.lang.Object v06;
    ch.epfl.rechor.IcalBuilder.Name[] v07;
    ch.epfl.rechor.IcalBuilder.Component[] v08;
    ch.epfl.rechor.journey.Journey v09;
    int v10;
    long v11;
    boolean v12;
}
