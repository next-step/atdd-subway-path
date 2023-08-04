package nextstep.subway.utils;

import nextstep.subway.domain.Station;

import java.util.Map;

public class StationFixture {
    public static final Station 신사역 = new Station("신사역");
    public static final Station 논현역 = new Station("논현역");
    public static final Station 강남역 = new Station("강남역");
    public static final Station 양재역 = new Station("양재역");
    public static final Station 양재시민의숲역 = new Station("양재시민의숲역");
    public static final Station 서울역 = new Station("서울역");
    public static final Station 용산역 = new Station("용산역");
    public static final Station 교대역 = new Station("교대역");
    public static final Station 남부터미널역 = new Station("남부터미널역");

    public static final Map<Station, Long> stationIds = Map.of(
            신사역, 1L,
            논현역, 2L,
            강남역, 3L,
            양재역, 4L,
            양재시민의숲역, 5L,
            교대역, 6L,
            남부터미널역, 7L
    );
}
