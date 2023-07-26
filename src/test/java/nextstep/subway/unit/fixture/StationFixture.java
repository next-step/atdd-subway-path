package nextstep.subway.unit.fixture;

import nextstep.subway.station.repository.Station;

public class StationFixture {
    public static final Long 강남역_ID = 1L;
    public static final Long 신논현역_ID = 2L;
    public static final Long 논현역_ID = 3L;

    public static Station 강남역() {
        return new Station("강남역");
    }
    public static Station 신논현역() {
        return new Station("신논현역");
    }
    public static Station 논현역() {
        return new Station("논현역");
    }
}
