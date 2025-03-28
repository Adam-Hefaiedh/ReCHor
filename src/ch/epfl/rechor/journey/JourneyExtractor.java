package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class JourneyExtractor {

    private JourneyExtractor(){}

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

    private static void createJourney(long criteria, int depStationId, Profile profile, List<Journey.Leg> legs){
        Connections connections = profile.connections();
        TimeTable timeTable = profile.timeTable();
        int intermediateChanges = Bits32_24_8.unpack8(PackedCriteria.payload(criteria));
        int ConnectionID = Bits32_24_8.unpack24(PackedCriteria.payload(criteria));
        int arrStationid;
        long newCriteria;
        if(depStationId != convertToStation(timeTable, connections.depStopId(ConnectionID))){

            createFoot(profile,depStationId,connections.depStopId(ConnectionID),
                    PackedCriteria.depMins(criteria), legs);
        }
        for(int i=1; i<PackedCriteria.changes(criteria)+1; i++){
            ConnectionID = createTransport(profile, ConnectionID, legs, intermediateChanges);
            arrStationid = connections.arrStopId(ConnectionID);
                newCriteria =
                        profile.forStation(convertToStation(timeTable, arrStationid))
                                .get(PackedCriteria.arrMins(criteria),PackedCriteria.changes(criteria) - i);
                int arrHours = connections.arrMins(ConnectionID);
                ConnectionID = Bits32_24_8.unpack24(PackedCriteria.payload(newCriteria));
                intermediateChanges = Bits32_24_8.unpack8(PackedCriteria.payload(newCriteria));
                createFoot(profile, arrStationid,connections.depStopId(ConnectionID), arrHours,legs);
        }
        ConnectionID = createTransport(profile, ConnectionID, legs, intermediateChanges);
        arrStationid = connections.arrStopId(ConnectionID);
        if(convertToStation(timeTable, connections.arrStopId(ConnectionID)) != profile.arrStationId()){
            createFoot(profile, arrStationid, profile.arrStationId(), connections.arrMins(ConnectionID),legs);
        }
    }
    private static int createTransport(Profile profile, int ConnectionID, List<Journey.Leg> legs, int intermediateChanges)
    {
        TimeTable timeTable = profile.timeTable();
        Stations stations = timeTable.stations();
        Connections connections = profile.connections();
        Trips trips = profile.trips();
        Routes routes = timeTable.routes();
        LocalDate date = profile.date();
        List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
        Stop depStop = createStop(stations, timeTable, connections.depStopId(ConnectionID));
        Stop arrStop = createStop(stations, timeTable, connections.arrStopId(ConnectionID));
        LocalDateTime beginDepTime = formatDate(date, connections.depMins(ConnectionID));
        LocalDateTime depTime = formatDate(date, connections.arrMins(ConnectionID));
        LocalDateTime arrTime;
        for(int j=0; j< intermediateChanges; j++){
            ConnectionID = connections.nextConnectionId(ConnectionID);
            arrTime = formatDate(date,connections.depMins(ConnectionID));
            intermediateStops.add(new Journey.Leg.IntermediateStop(arrStop,depTime,arrTime));
            depTime = formatDate(date,connections.arrMins(ConnectionID));
            arrStop = createStop(stations, timeTable, connections.arrStopId(ConnectionID));
        }
        arrTime = depTime;
        depTime = beginDepTime;
        Journey.Leg.Transport transport = new Journey.Leg.Transport(depStop, depTime, arrStop, arrTime,
                intermediateStops,routes.vehicle(trips.routeId(connections.tripId(ConnectionID))),
                routes.name(trips.routeId(connections.tripId(ConnectionID))),
                trips.destination(connections.tripId(ConnectionID)));
        legs.add(transport);
        return ConnectionID;
    }

    private static void createFoot(Profile profile, int depStationId, int arrStationID, int depHour, List<Journey.Leg> legs){
        TimeTable timeTable = profile.timeTable();
        Stations stations = timeTable.stations();
        Transfers transfers = timeTable.transfers();
        LocalDate date = profile.date();
        int station = convertToStation(timeTable, arrStationID);
        int station2 = convertToStation(timeTable, depStationId);
            Stop depStop = createStop(stations,  timeTable, depStationId);
            Stop arrStop =createStop(stations,  timeTable, arrStationID);

            LocalDateTime arrTime = formatDate(date,depHour
                    + transfers.minutesBetween(station2, station));
            LocalDateTime depTime = formatDate(date, depHour);
            Journey.Leg.Foot foot = new Journey.Leg.Foot(depStop,depTime,arrStop,arrTime);
            legs.add(foot);

    }

    private static LocalDateTime formatDate(LocalDate date, int time){
        return date.atStartOfDay().plusMinutes(time);
    }

    private static int convertToStation(TimeTable timeTable, int stopId)
    {
        return timeTable.isPlatformId(stopId) ? timeTable.platforms().stationId(timeTable.stationId(stopId)) : stopId;
    }

    private static Stop createStop(Stations stations, TimeTable timeTable, int stopId){
        int station = convertToStation(timeTable, stopId);
       return new Stop(
                stations.name(station),
                timeTable.platformName(stopId),
                stations.longitude(station),
                stations.latitude(station));
    }
}
