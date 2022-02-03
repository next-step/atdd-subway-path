package nextstep.subway.unit.test.utils;

import nextstep.subway.domain.Station;

public class Stations {
    public static Station 연신내;
    public static Station 서울역;
    public static Station 삼성역;
    public static Station 판교역;

    public static void 노선_초기화() {
        연신내 = new Station("연신내");
        서울역 = new Station("서울역");
        삼성역 = new Station("삼성역");
        판교역 = new Station("판교역");
    }
}
