package nextstep.subway.unit;

import nextstep.subway.domain.Station;

import java.util.Map;

public class StationFixture {
    public static final Station 신사역 = new Station("신사역");
    public static final Station 논현역 = new Station("논현역");
    public static final Station 강남역 = new Station("강남역");
    public static final Station 양재역 = new Station("양재역");
    public static final Station 양재시민의숲역 = new Station("양재시민의숲역");

    public static final Map<Station, Long> stationIds = Map.of(
            신사역, 1L,
            논현역, 2L,
            강남역, 3L,
            양재역, 4L,
            양재시민의숲역, 5L
    );
}
