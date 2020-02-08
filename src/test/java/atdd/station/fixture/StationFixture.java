package atdd.station.fixture;

public class StationFixture {
    public static final String KANGNAM_STATION_NAME = "강남역";
    public static final String SINSA = "신사역";

    public static final atdd.station.domain.Station KANNAM_STATON = new atdd.station.domain.Station(KANGNAM_STATION_NAME);
    public static final atdd.station.domain.Station SINSA_STATION = new atdd.station.domain.Station(SINSA);
}
