package atdd.station.fixture;

import atdd.station.domain.Station;

import static atdd.station.fixture.SubwaysFixture.SUBWAYS;

public class StationFixture {
    public static final String KANGNAM_STATION_NAME = "강남역";
    public static final String PANGYO_STATION_NAME = "판교역";

    public static final Station KANNAM_STATON = new Station(KANGNAM_STATION_NAME);
    public static final Station PANGYO = new Station(PANGYO_STATION_NAME);

    public static Station getStation(String name) {
        return Station.builder()
                .name(name)
                .subways(SUBWAYS)
                .build();
    }
}
