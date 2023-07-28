package nextstep.subway.fixture;

import nextstep.subway.domain.Station;

public class StationFixture {
    public static final String 교대역_이름 = "교대역";
    public static final String 강남역_이름 = "강남역";
    public static final String 역삼역_이름 = "역삼역";
    public static final String 선릉역_이름 = "선릉역";
    public static final String 삼성역_이름 = "삼성역";

    public static final Station 교대역 = new Station(교대역_이름);
    public static final Station 강남역 = new Station(강남역_이름);
    public static final Station 역삼역 = new Station(역삼역_이름);
    public static final Station 선릉역 = new Station(선릉역_이름);
    public static final Station 삼성역 = new Station(삼성역_이름);
}
