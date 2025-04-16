package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * La classe représente un « extracteur de voyages ».
 *
 * @author Adam Taieb Hefaiedh (391029).
 * @author El Hassan Lyoussi (398924).
 */
public class JourneyExtractor {

    private JourneyExtractor(){}

    /**
     * Retourne la totalité des voyages optimaux correspondant au profil et à la gare de départ donnés,
     * triés d'abord par heure de départ (croissante) puis par heure d'arrivée (croissante).
     * @param profile Profile contenant les voyages optimaux vers une gare d'index donnée.
     * @param depStationId Index de gare dont le voyage commence.
     * @return la totalité des voyages optimaux correspondant au profil et à la gare de départ donnés.
     */
    public static List<Journey> journeys(Profile profile, int depStationId){
        ParetoFront pf = profile.forStation(depStationId);
        List<Journey> journeys = new ArrayList<>();
        pf.forEach((long criteria) -> {
            List<Journey.Leg> legs = new ArrayList<>();
            createJourney(criteria,depStationId,profile,legs);
            journeys.add(new Journey(legs));
        });
        journeys.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));
        return journeys;
    }

    private static void createJourney(long criteria, int depStationId, Profile profile,
                                      List<Journey.Leg> legs){
        Connections connections = profile.connections();
        TimeTable timeTable = profile.timeTable();
        int intermediateChanges = Bits32_24_8.unpack8(PackedCriteria.payload(criteria));
        int connectionId = Bits32_24_8.unpack24(PackedCriteria.payload(criteria));
        int arrStationId;
        long newCriteria;
        if(depStationId != timeTable.stationId(connections.depStopId(connectionId))){

            createFoot(profile,depStationId,connections.depStopId(connectionId),
                    PackedCriteria.depMins(criteria), legs);
        }
        for(int i=1; i<PackedCriteria.changes(criteria)+1; i++){
            connectionId = createTransport(profile, connectionId, legs, intermediateChanges);
            arrStationId = connections.arrStopId(connectionId);
                newCriteria =
                        profile.forStation(timeTable.stationId(arrStationId))
                                .get(PackedCriteria.arrMins(criteria),
                                        PackedCriteria.changes(criteria) - i);
                int arrHours = connections.arrMins(connectionId);
                connectionId = Bits32_24_8.unpack24(PackedCriteria.payload(newCriteria));
                intermediateChanges = Bits32_24_8.unpack8(PackedCriteria.payload(newCriteria));
                createFoot(profile, arrStationId,connections.depStopId(connectionId), arrHours,legs);
        }
        connectionId = createTransport(profile, connectionId, legs, intermediateChanges);
        arrStationId = connections.arrStopId(connectionId);
        if(timeTable.stationId(connections.arrStopId(connectionId)) != profile.arrStationId()){
            createFoot(profile, arrStationId, profile.arrStationId(),
                    connections.arrMins(connectionId),legs);
        }
    }
    private static int createTransport(Profile profile, int connectionId, List<Journey.Leg> legs,
                                       int intermediateChanges)
    {
        TimeTable timeTable = profile.timeTable();
        Stations stations = timeTable.stations();
        Connections connections = profile.connections();
        Trips trips = profile.trips();
        Routes routes = timeTable.routes();
        LocalDate date = profile.date();
        List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
        Stop depStop = createStop(stations, timeTable, connections.depStopId(connectionId));
        Stop arrStop = createStop(stations, timeTable, connections.arrStopId(connectionId));
        LocalDateTime beginDepTime = formatDate(date, connections.depMins(connectionId));
        LocalDateTime depTime = formatDate(date, connections.arrMins(connectionId));
        LocalDateTime arrTime;
        for(int j=0; j< intermediateChanges; j++){
            connectionId = connections.nextConnectionId(connectionId);
            arrTime = formatDate(date,connections.depMins(connectionId));
            intermediateStops.add(new Journey.Leg.IntermediateStop(arrStop,depTime,arrTime));
            depTime = formatDate(date,connections.arrMins(connectionId));
            arrStop = createStop(stations, timeTable, connections.arrStopId(connectionId));
        }
        arrTime = depTime;
        depTime = beginDepTime;
        Journey.Leg.Transport transport = new Journey.Leg.Transport(depStop, depTime, arrStop, arrTime,
                intermediateStops,routes.vehicle(trips.routeId(connections.tripId(connectionId))),
                routes.name(trips.routeId(connections.tripId(connectionId))),
                trips.destination(connections.tripId(connectionId)));
        legs.add(transport);
        return connectionId;
    }

    private static void createFoot(Profile profile, int depStationId,
                                   int arrStationId, int depHour, List<Journey.Leg> legs){
        TimeTable timeTable = profile.timeTable();
        Stations stations = timeTable.stations();
        Transfers transfers = timeTable.transfers();
        LocalDate date = profile.date();
        int station = timeTable.stationId(arrStationId);
        int station2 = timeTable.stationId(depStationId);
        Stop depStop = createStop(stations,  timeTable, depStationId);
        Stop arrStop = createStop(stations, timeTable, arrStationId);
        System.out.println(timeTable.stations().name(station));
        System.out.println(timeTable.stations().name(station2));
        LocalDateTime arrTime = formatDate(date, depHour
                + transfers.minutesBetween(station2, station));
        LocalDateTime depTime = formatDate(date, depHour);
        Journey.Leg.Foot foot = new Journey.Leg.Foot(depStop, depTime, arrStop, arrTime);
        legs.add(foot);

    }

    private static LocalDateTime formatDate(LocalDate date, int time){
        return date.atStartOfDay().plusMinutes(time);
    }



    private static Stop createStop(Stations stations, TimeTable timeTable, int stopId){
        int station = timeTable.stationId(stopId);
       return new Stop(
                stations.name(station),
                timeTable.platformName(stopId),
                stations.longitude(station),
                stations.latitude(station));
    }
}
