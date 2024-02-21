package nextstep.subway.support.fixture;

import java.util.Map;

public class StationFixture {


    public static final String 서울역 = "서울역";
    public static final String 청량리역 = "청량리역";
    public static final String 강남역 = "강남역";
    public static final String 교대역 = "교대역";
    public static final String 낙성대 = "낙성대역";
    public static final String 봉천역 = "봉천역";
    public static final String 서울대입구역 = "서울대입구역";
    public static final String NAME = "name";

    public static Map<String, Object> 서울역_생성() {
        return 지허철역_생성(서울역);
    }

    public static Map<String, Object> 청량리역_생성() {
        return 지허철역_생성(청량리역);
    }

    public static Map<String, Object> 강남역_생성() {
        return 지허철역_생성(강남역);
    }

    public static Map<String, Object> 교대역_생성() {
        return 지허철역_생성(교대역);
    }


    public static Map<String, Object> 낙성대역_생성() {
        return 지허철역_생성(낙성대);
    }

    public static Map<String, Object> 서울대입구역_생성() {
        return 지허철역_생성(서울대입구역);
    }

    public static Map<String, Object> 봉천역_생성() {
        return 지허철역_생성(봉천역);
    }

    public static Map<String, Object> 지허철역_생성(String name) {
        return Map.of(NAME, name);
    }

}
