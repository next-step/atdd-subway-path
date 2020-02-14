package atdd.station.fixture;

import atdd.station.domain.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static atdd.station.fixture.SubwaysFixture.SUBWAYS;

public class StationFixture {
    public static final String KANGNAM_STATION_NAME = "강남역";
    public static final String YUCKSAM_STATION_NAME = "역삼역";
    public static final String SUNLENG_STATION_NAME = "선릉역";
    public static final String PANGYO_STATION_NAME = "판교역";

    public static final Station KANNAM_STATON = new Station(KANGNAM_STATION_NAME);
    public static final Station YUCKSAM_STATON = new Station(YUCKSAM_STATION_NAME);
    public static final Station SUNLENG_STATON = new Station(SUNLENG_STATION_NAME);

    public static final List<Station> KANGNAM_AND_YUCKSAM_STATIONS = new ArrayList<>(Arrays.asList(KANNAM_STATON, YUCKSAM_STATON));

    public static final Station PANGYO = new Station(PANGYO_STATION_NAME);

    public static Station getStation(String name) {
        return Station.builder()
                .name(name)
                .subways(SUBWAYS)
                .build();
    }
}
