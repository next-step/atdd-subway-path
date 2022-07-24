package nextstep.subway.fake;

import nextstep.subway.domain.Station;

public class FakeStationFactory {
    public static Station 강남역() {
      return new Station(1L, "강남역");
    }

    public static Station 선릉역() {
        return new Station(2L, "선릉역");
    }

    public static Station 영통역() {
        return new Station(3L, "영통역");
    }

    public static Station 구의역() {
        return new Station(4L, "구의역");
    }

    public static Station 신촌역() {
        return new Station(5L, "신촌역");
    }
    public static Station 역삼역() {
        return new Station(6L, "역삼역");
    }
}
