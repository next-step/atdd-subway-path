package atdd.station.fixture;

import atdd.station.domain.Station;

public class StationFixture {
    public static final String KANGNAM_STATION_NAME = "강남역";
    public static final String SINSA_STATION_NAME = "신사역";

    public static final Station KANNAM_STATON = new Station(KANGNAM_STATION_NAME);
    public static final Station SINSA_STATION = new Station(SINSA_STATION_NAME);
}
