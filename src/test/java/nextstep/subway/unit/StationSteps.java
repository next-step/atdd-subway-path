package nextstep.subway.unit;

import nextstep.subway.domain.Station;

public class StationSteps {
    public static Station 강남역() {
        return new Station(1L, "강남역");
    }

    public static Station 사당역() {
        return new Station(2L, "사당역");
    }

    public static Station 대림역() {
        return new Station(3L, "대림역");
    }

    public static Station 신도림역() {
        return new Station(4L, "신도림역");
    }

    public static Station 서울시청역() {
        return new Station(5L, "서울시청역");
    }
}
