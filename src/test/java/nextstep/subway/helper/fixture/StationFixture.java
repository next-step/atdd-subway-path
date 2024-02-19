package nextstep.subway.helper.fixture;

import nextstep.subway.domain.Station;

import java.util.Map;

public class StationFixture {
    // TODO: 아래 Map<> 들 정리 필요...
    public static Map<String, String> 강남역 = Map.of("name", "강남역");
    public static Map<String, String> 신논현역 = Map.of("name", "신논현역");
    public static Map<String, String> 논현역 = Map.of("name", "논현역");
    public static Map<String, String> 역삼역 = Map.of("name", "역삼역");

    public static Map<String, String> 논현역_생성_바디 = Map.of("name", "논현역");
    public static Map<String, String> 신사역_생성_바디 = Map.of("name", "신사역");
    public static Map<String, String> 신논현역_생성_바디 = Map.of("name", "신논현역");
    public static Map<String, String> 고속터미널역_생성_바디 = Map.of("name", "고속터미널역");
    public static Map<String, String> 한남역_생성_바디 = Map.of("name", "한남역");
    public static Map<String, String> 서빙고역_생성_바디 = Map.of("name", "서빙고역");

    public static Station 강남역_엔티티 = new Station(1L, "강남역");
    public static Station 논현역_엔티티 = new Station(2L, "논현역");
    public static Station 신논현역_엔티티 = new Station(3L, "신논현역");
    public static Station 신사역_엔티티 = new Station(4L, "신사역");
    public static Station 고속터미널역_엔티티 = new Station(5L, "고속터미널역");
    public static Station 역삼역_엔티티 = new Station(6L, "역삼역");
    public static Station 선릉역_엔티티 = new Station(7L, "선릉역");
    public static Station 삼성역_엔티티 = new Station(8L, "삼성역");
}
