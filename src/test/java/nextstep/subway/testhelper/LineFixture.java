package nextstep.subway.testhelper;

import java.util.HashMap;
import java.util.Map;

public class LineFixture {
    public static final String 신분당선 = "신분당선";
    public static final String 영호선 = "0호선";
    private Map<String, String> 신분당선_강남역_부터_삼성역_params;
    private Map<String, String> 영호선_강남역_부터_삼성역_params;
    private Map<String, String> 일호선_잠실역_부터_강남역_params;
    private Map<String, String> 이호선_강남역_부터_삼성역_params;
    private Map<String, String> 삼호선_잠실역_부터_선릉역_params;
    private Map<String, String> 사호선_교대역_부터_서초역_params;

    public LineFixture(StationFixture stationFixture) {
        신분당선_강남역_부터_삼성역_params = createParams(신분당선, "bg-red-600", stationFixture.get강남역_ID(), stationFixture.get삼성역_ID(), 10L);
        영호선_강남역_부터_삼성역_params = createParams(영호선, "bg-red-100", stationFixture.get강남역_ID(), stationFixture.get선릉역_ID(), 10L);
        일호선_잠실역_부터_강남역_params = createParams("일호선", "blue", stationFixture.get잠실역_ID(), stationFixture.get강남역_ID(), 10L);
        이호선_강남역_부터_삼성역_params = createParams("이호선", "green", stationFixture.get강남역_ID(), stationFixture.get삼성역_ID(), 10L);
        삼호선_잠실역_부터_선릉역_params = createParams("삼호선", "orange", stationFixture.get잠실역_ID(), stationFixture.get선릉역_ID(), 2L);
        사호선_교대역_부터_서초역_params = createParams("사호선", "skyBlue", stationFixture.get교대역_ID(), stationFixture.get서초역_ID(), 5L);
    }

    public static Map<String, String> createParams(String name,
                                                   String color,
                                                   Long upStationId,
                                                   Long downStationId,
                                                   Long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance.toString());

        return params;
    }

    public Map<String, String> get신분당선_강남역_부터_삼성역_params() {
        return 신분당선_강남역_부터_삼성역_params;
    }

    public Map<String, String> get영호선_강남역_부터_삼성역_params() {
        return 영호선_강남역_부터_삼성역_params;
    }

    public Map<String, String> get일호선_잠실역_부터_강남역_params() {
        return 일호선_잠실역_부터_강남역_params;
    }

    public Map<String, String> get이호선_강남역_부터_삼성역_params() {
        return 이호선_강남역_부터_삼성역_params;
    }

    public Map<String, String> get삼호선_잠실역_부터_선릉역_params() {
        return 삼호선_잠실역_부터_선릉역_params;
    }

    public Map<String, String> get사호선_교대역_부터_서초역_params() {
        return 사호선_교대역_부터_서초역_params;
    }
}
