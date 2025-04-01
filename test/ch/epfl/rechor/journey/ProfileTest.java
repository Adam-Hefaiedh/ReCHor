package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {
    @Test
    void profileConstructorCopiesStationFront() {
        var timeTable = new FakeTimeTable();
        var date = LocalDate.of(2025, Month.MARCH, 28);
        var arrStationId = 1;
        var stationFront = new ArrayList<ParetoFront>();
        var profile = new Profile(timeTable, date, arrStationId, stationFront);
        stationFront.add(ParetoFront.EMPTY);
        assertTrue(profile.stationFront().isEmpty());
    }

    @Test
    void profileStationFrontIsImmutable() {
        var timeTable = new FakeTimeTable();
        var date = LocalDate.of(2025, Month.MARCH, 28);
        var arrStationId = 1;
        var stationFront = List.of(ParetoFront.EMPTY);
        var profile = new Profile(timeTable, date, arrStationId, stationFront);
        try {
            profile.stationFront().add(ParetoFront.EMPTY);
            assertEquals(1, profile.stationFront().size());
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    void profileConnectionsWorks() {
        var timeTable = new FakeTimeTable();
        var arrStationId = 1;
        var stationFront = List.of(ParetoFront.EMPTY);
        var date = LocalDate.of(2025, Month.MARCH, 28);
        for (var i = 0; i < 10; i += 1) {
            var profile = new Profile(timeTable, date, arrStationId, stationFront);
            var connections = profile.connections();
            assertEquals(date, timeTable.lastDate);
            assertEquals(0, connections.size());
            date = date.plusDays(1);
        }
    }

    @Test
    void profileTripsWorks() {
        var timeTable = new FakeTimeTable();
        var arrStationId = 1;
        var stationFront = List.of(ParetoFront.EMPTY);
        var date = LocalDate.of(2025, Month.MARCH, 28);
        for (var i = 0; i < 10; i += 1) {
            var profile = new Profile(timeTable, date, arrStationId, stationFront);
            var trips = profile.trips();
            assertEquals(date, timeTable.lastDate);
            assertEquals(0, trips.size());
            date = date.plusDays(1);
        }
    }

    @Test
    void profileForStationThrowsWithInvalidIndex() {
        var timeTable = new FakeTimeTable();
        var date = LocalDate.of(2025, Month.MARCH, 28);
        var arrStationId = 1;
        var stationFront = List.of(ParetoFront.EMPTY);
        var profile = new Profile(timeTable, date, arrStationId, stationFront);
        assertThrows(IndexOutOfBoundsException.class, () -> profile.forStation(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> profile.forStation(1));
    }

    @Test
    void profileForStationWorks() {
        var paretoFronts = new ArrayList<ParetoFront>();
        for (var i = 0; i < 10; i += 1)
            paretoFronts.add(new ParetoFront.Builder()
                    .add(i, i, 0)
                    .build());
        var timeTable = new FakeTimeTable();
        var date = LocalDate.of(2025, Month.MARCH, 28);
        var arrStationId = 1;
        var stationFront = List.copyOf(paretoFronts);
        var profile = new Profile(timeTable, date, arrStationId, stationFront);
        for (var i = 0; i < 10; i += 1) assertSame(paretoFronts.get(i), profile.forStation(i));
    }

    @Test
    void profileBuilderForStationReturnsNullForAllValidIndices() {
        for (var stationsSize = 1; stationsSize < 100; stationsSize += 1) {
            var timeTable = new FakeTimeTable(stationsSize, 0);
            var date = LocalDate.of(2025, Month.MARCH, 28);
            var arrStationId = 1;
            var builder = new Profile.Builder(timeTable, date, arrStationId);
            for (var i = 0; i < stationsSize; i += 1) assertNull(builder.forStation(i));
        }
    }

    @Test
    void profileBuilderForStationThrowsOnInvalidIndex() {
        var timeTable = new FakeTimeTable(10, 0);
        var date = LocalDate.of(2025, Month.MARCH, 28);
        var arrStationId = 1;
        var builder = new Profile.Builder(timeTable, date, arrStationId);
        assertThrows(IndexOutOfBoundsException.class, () -> builder.forStation(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.forStation(10));
    }

    @Test
    void profileBuilderSetForStationWorksForAllValidIndices() {
        var stationsSize = 10;
        var timeTable = new FakeTimeTable(stationsSize, 0);
        var date = LocalDate.of(2025, Month.MARCH, 28);
        var arrStationId = 1;
        var builder = new Profile.Builder(timeTable, date, arrStationId);

        var paretoBuilders = new ArrayList<ParetoFront.Builder>();
        for (var i = 0; i < stationsSize; i += 1) {
            var paretoBuilder = new ParetoFront.Builder();
            paretoBuilders.add(paretoBuilder);
            builder.setForStation(i, paretoBuilder);
        }

        for (var i = 0; i < stationsSize; i += 1)
            assertSame(paretoBuilders.get(i), builder.forStation(i));
    }

    @Test
    void profileBuilderForTripReturnsNullForAllValidIndices() {
        for (var tripsSize = 1; tripsSize < 100; tripsSize += 1) {
            var timeTable = new FakeTimeTable(0, tripsSize);
            var date = LocalDate.of(2025, Month.MARCH, 28);
            var arrStationId = 1;
            var builder = new Profile.Builder(timeTable, date, arrStationId);
            for (var i = 0; i < tripsSize; i += 1) assertNull(builder.forTrip(i));
        }
    }

    @Test
    void profileBuilderForTripThrowsOnInvalidIndex() {
        var timeTable = new FakeTimeTable(0, 10);
        var date = LocalDate.of(2025, Month.MARCH, 28);
        var arrStationId = 1;
        var builder = new Profile.Builder(timeTable, date, arrStationId);
        assertThrows(IndexOutOfBoundsException.class, () -> builder.forTrip(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.forTrip(10));
    }

    @Test
    void profileBuilderSetForTripWorksForAllValidIndices() {
        var tripsSize = 10;
        var timeTable = new FakeTimeTable(0, tripsSize);
        var date = LocalDate.of(2025, Month.MARCH, 28);
        var arrStationId = 1;
        var builder = new Profile.Builder(timeTable, date, arrStationId);

        var paretoBuilders = new ArrayList<ParetoFront.Builder>();
        for (var i = 0; i < tripsSize; i += 1) {
            var paretoBuilder = new ParetoFront.Builder();
            paretoBuilders.add(paretoBuilder);
            builder.setForTrip(i, paretoBuilder);
        }

        for (var i = 0; i < tripsSize; i += 1)
            assertSame(paretoBuilders.get(i), builder.forTrip(i));
    }

    @Test
    void profileBuilderBuildWorks() {
        var stationsSize = 10;
        var timeTable = new FakeTimeTable(stationsSize, 5);
        var date = LocalDate.of(2025, Month.MARCH, 28);
        var arrStationId = 1;
        var builder = new Profile.Builder(timeTable, date, arrStationId);
        for (var i = 0; i < stationsSize; i += 1) {
            if (i % 2 == 0)
                builder.setForStation(i, new ParetoFront.Builder().add(i, i, 0));
        }
        var profile = builder.build();
        for (var i = 0; i < stationsSize; i += 1) {
            var finalI = i;
            if (i % 2 == 0) {
                var front = profile.forStation(i);
                assertEquals(1, front.size());
                front.forEach(c -> {
                    assertEquals(finalI, PackedCriteria.arrMins(c));
                    assertEquals(finalI, PackedCriteria.changes(c));
                });
            } else {
                assertEquals(0, profile.forStation(i).size());
            }
        }
    }

    private static class FakeTimeTable implements TimeTable {
        private final int stationsSize;
        private final int tripsSize;
        LocalDate lastDate = null;

        public FakeTimeTable(int stationsSize, int tripsSize) {
            this.stationsSize = stationsSize;
            this.tripsSize = tripsSize;
        }

        public FakeTimeTable() {
            this(0, 0);
        }

        @Override
        public Platforms platforms() {
            return new Platforms() {
                @Override
                public String name(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int stationId(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int size() {
                    return 0;
                }
            };
        }

        @Override
        public Stations stations() {
            return new Stations() {
                @Override
                public String name(int id) {
                    return "station" + id;
                }

                @Override
                public double longitude(int id) {
                    return (id % 360) - 180;
                }

                @Override
                public double latitude(int id) {
                    return (id % 180) - 90;
                }

                @Override
                public int size() {
                    return stationsSize;
                }
            };
        }

        @Override
        public StationAliases stationAliases() {
            return new StationAliases() {
                @Override
                public String alias(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public String stationName(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int size() {
                    return 0;
                }
            };
        }

        @Override
        public Transfers transfers() {
            return new Transfers() {
                @Override
                public int depStationId(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int minutes(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int arrivingAt(int stationId) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int minutesBetween(int depStationId, int arrStationId) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int size() {
                    return 0;
                }
            };
        }

        @Override
        public Routes routes() {
            return new Routes() {
                @Override
                public Vehicle vehicle(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public String name(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int size() {
                    return 0;
                }
            };
        }

        @Override
        public Connections connectionsFor(LocalDate date) {
            lastDate = date;
            return new Connections() {
                @Override
                public int depStopId(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int depMins(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int arrStopId(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int arrMins(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int tripId(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int tripPos(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int nextConnectionId(int id) {
                    throw new IndexOutOfBoundsException();
                }

                @Override
                public int size() {
                    return 0;
                }
            };
        }

        @Override
        public Trips tripsFor(LocalDate date) {
            lastDate = date;
            return new Trips() {
                @Override
                public int routeId(int id) {
                    return id / 10;
                }

                @Override
                public String destination(int id) {
                    return "destination" + id;
                }

                @Override
                public int size() {
                    return tripsSize;
                }
            };
        }
    }
}