package nextstep.subway.unit;

import org.springframework.test.util.ReflectionTestUtils;

import nextstep.subway.domain.station.Station;

public class StationFixture {
    public static final Station 교대역 = makeStation(1L, "교대역");
    public static final Station 강남역 = makeStation(2L, "강남역");
    public static final Station 역삼역 = makeStation(3L, "역삼역");
    public static final Station 선릉역 = makeStation(4L, "선릉역");
    public static final Station 삼성역 = makeStation(5L, "삼성역");

    private static Station makeStation(final Long id, final String name) {
        final var station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
