package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JourneyExtractor {

    private JourneyExtractor(){}

    public static List<Journey> journeys(Profile profile, int depStationId){
        ParetoFront pf = profile.forStation(depStationId);
        List<Journey> journeys = new ArrayList<Journey>();
        pf.forEach((long criteria) -> {
            List<Journey.Leg> legs = new ArrayList<Journey.Leg>();
            int ConnectionID = Bits32_24_8.unpack24(PackedCriteria.payload(criteria));
            System.out.println(ConnectionID);
            createFoot(profile,depStationId,ConnectionID,legs);
            createTransport(criteria,profile,ConnectionID,legs);
            journeys.add(new Journey(legs));
        });
        journeys.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));
        return journeys;
    }

    private static void createTransport(long criteria, Profile profile, int ConnectionID, List<Journey.Leg> legs){
        Stations stations = profile.timeTable().stations();
        StationAliases aliases = profile.timeTable().stationAliases();
        Connections connections = profile.connections();
        Trips trips = profile.trips();
        Routes routes = profile.timeTable().routes();
        int arrStationid = connections.arrStopId(ConnectionID);
        for(int i=0; i<PackedCriteria.changes(criteria); i++){
            List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
            Stop depStop = createStop(stations, aliases, profile.timeTable(), connections.depStopId(ConnectionID));
            Stop arrStop = createStop(stations, aliases,  profile.timeTable(), connections.arrStopId(ConnectionID));
            LocalDateTime depTime1 = timeToDate(profile, connections.depMins(ConnectionID));
            LocalDateTime depTime = timeToDate(profile, connections.arrMins(ConnectionID));
            ConnectionID = connections.nextConnectionId(ConnectionID);
            LocalDateTime arrTime = timeToDate(profile,connections.depMins(ConnectionID));
            for(int j=0; j< Bits32_24_8.unpack8(PackedCriteria.payload(criteria)); j++){
                intermediateStops.add(new Journey.Leg.IntermediateStop(arrStop,depTime,arrTime));
                arrStationid = connections.arrStopId(ConnectionID);
                depTime = timeToDate(profile,connections.arrMins(ConnectionID));
                arrStop = createStop(stations, aliases,  profile.timeTable(), connections.arrStopId(ConnectionID));
                ConnectionID = connections.nextConnectionId(ConnectionID);
                arrTime = timeToDate(profile,connections.depMins(ConnectionID));
            }
            arrTime = depTime;
            depTime = depTime1;
            Journey.Leg.Transport transport = new Journey.Leg.Transport(depStop, depTime, arrStop, arrTime,
                    intermediateStops,routes.vehicle(trips.routeId(connections.tripId(ConnectionID))),
                    routes.name(trips.routeId(connections.tripId(ConnectionID))),
                    trips.destination(connections.tripId(ConnectionID)));
            legs.add(transport);
            ConnectionID = Bits32_24_8.unpack24(PackedCriteria.payload(
                        profile.forStation(profile.timeTable().stationId(connections.arrStopId(ConnectionID)))
                                .get(PackedCriteria.arrMins(criteria),PackedCriteria.changes(criteria) - i - 1)));
            ConnectionID = connections.nextConnectionId(ConnectionID);
            createFoot(profile,arrStationid, ConnectionID,legs);
        }
    }

    private static void createFoot(Profile profile, int depStationId, int ConnectionID, List<Journey.Leg> legs){
        Stations stations = profile.timeTable().stations();
        StationAliases aliases = profile.timeTable().stationAliases();
        Connections connections = profile.connections();
        Transfers transfers = profile.timeTable().transfers();
        for(int i= 0; i<connections.size(); i++)
        {
//            if(stations.name(connections.depStopId(ConnectionID))){
//
//            }
        }
        System.out.println(stations.name(connections.depStopId(ConnectionID)));
        System.out.println(stations.name(depStationId));
        if(connections.depStopId(ConnectionID) != depStationId){
            Stop depStop = createStop(stations, aliases,  profile.timeTable(), depStationId);
            Stop arrStop =createStop(stations, aliases,  profile.timeTable(), connections.depStopId(ConnectionID));
            LocalDateTime arrTime = timeToDate(profile, connections.depMins(ConnectionID));
            LocalDateTime depTime = timeToDate(profile, connections.depMins(ConnectionID)
            - transfers.minutesBetween(depStationId, connections.depStopId(ConnectionID)));
            Journey.Leg.Foot foot = new Journey.Leg.Foot(depStop,depTime,arrStop,arrTime);
            legs.add(foot);
        }

    }

    private static LocalDateTime timeToDate(Profile profile, int time){
        return  LocalDateTime.of(profile.date(),
                LocalTime.of(time/60,
                        time%60));
    }
    private static Stop createStop(Stations stations,StationAliases aliases, TimeTable timeTable, int stopId){
       return new Stop(
                stations.name(timeTable.stationId(stopId)),
                timeTable.platformName(stopId),
                stations.longitude(timeTable.stationId(stopId)),
                stations.latitude(timeTable.stationId(stopId)));
    }
}
