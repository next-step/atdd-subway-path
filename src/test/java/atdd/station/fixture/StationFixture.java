package atdd.station.fixture;

import atdd.station.domain.Station;
import atdd.station.dto.subwayLine.SubwayLineUpdateRequestDto;

import java.util.Arrays;
import java.util.List;

import static atdd.station.fixture.SubwaysFixture.SUBWAYS;

public class StationFixture {
    public static final String KANGNAM_STATION_NAME = "강남역";
    public static final String YUCKSAM_STATION_NAME = "역삼역";
    public static final String SUNLENG_STATION_NAME = "선릉역";
    public static final String EMAE_STATION_NAME = "아매역";
    public static final String PANGYO_STATION_NAME = "판교역";
    public static final String SEHEYN_STATION_NAME = "서현역";

    public static final Station KANGNAM_STATION = new Station(0L, KANGNAM_STATION_NAME);
    public static final Station YUCKSAM_STATON = new Station(1L, YUCKSAM_STATION_NAME);
    public static final Station SUNLENG_STATON = new Station(2L, SUNLENG_STATION_NAME);
    public static final Station PANGYO_STATION = new Station(3L, PANGYO_STATION_NAME);
    public static final Station EMAE_STATION = new Station(4L, EMAE_STATION_NAME);
    public static final Station SEHYEN_STATION = new Station(5L, SEHEYN_STATION_NAME);

    public static final List<Station> KANGNAM_AND_YUCKSAM_STATIONS = Arrays.asList(KANGNAM_STATION, YUCKSAM_STATON);
    public static final SubwayLineUpdateRequestDto KANGNAM_AND_YUCKSAM_STATIONS_DTO = SubwayLineUpdateRequestDto.toDtoEntity(Arrays.asList(KANGNAM_STATION, YUCKSAM_STATON));

    public static final Station PANGYO = new Station(PANGYO_STATION_NAME);

    public static Station getStation(String name) {
        return Station.builder()
                .name(name)
                .subways(SUBWAYS)
                .build();
    }
}
