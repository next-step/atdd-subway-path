package nextstep.subway.support.fixture;

import java.util.Map;
import nextstep.subway.domians.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class StationFixture {


    public static final String 서울역_이름 = "서울역";
    public static final String 청량리역_이름 = "청량리역";
    public static final String 강남역_이름 = "강남역";
    public static final String 교대역_이름 = "교대역";
    public static final String 낙성대역_이름 = "낙성대역";
    public static final String 봉천역_이름 = "봉천역";
    public static final String 서울대입구역_이름 = "서울대입구역";
    public static final String NAME = "name";

    public static Map<String, Object> 서울역_생성_요청() {
        return 지허철역_생성_요청(서울역_이름);
    }

    public static Map<String, Object> 청량리역_생성_요청() {
        return 지허철역_생성_요청(청량리역_이름);
    }

    public static Map<String, Object> 강남역_생성_요청() {
        return 지허철역_생성_요청(강남역_이름);
    }

    public static Map<String, Object> 교대역_생성_요청() {
        return 지허철역_생성_요청(교대역_이름);
    }


    public static Map<String, Object> 낙성대역_생성_요청() {
        return 지허철역_생성_요청(낙성대역_이름);
    }

    public static Map<String, Object> 서울대입구역_생성_요청() {
        return 지허철역_생성_요청(서울대입구역_이름);
    }

    public static Map<String, Object> 봉천역_생성_요청() {
        return 지허철역_생성_요청(봉천역_이름);
    }

    public static Map<String, Object> 지허철역_생성_요청(String name) {
        return Map.of(NAME, name);
    }


    public static Station giveOne(String name) {
        return new Station(name);
    }

    public static Station giveOne(long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
