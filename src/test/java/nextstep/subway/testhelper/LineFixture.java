package nextstep.subway.testhelper;

import java.util.HashMap;
import java.util.Map;

public class LineFixture {
    public static final String 신분당선 = "신분당선";
    public static final String 영호선 = "0호선";
    private Map<String, String> 신분당선_강남역_부터_삼성역_params;
    private Map<String, String> 영호선_강남역_부터_삼성역_params;
    private Long 일호선_잠실역_부터_강남역_ID;
    private Long 이호선_잠실역_부터_삼성역_ID;
    private Long 삼호선_강남역_부터_선릉역_ID;

    public LineFixture(StationFixture stationFixture) {
        신분당선_강남역_부터_삼성역_params = createParams(신분당선, "bg-red-600", stationFixture.get강남역_ID(), stationFixture.get삼성역_ID(), 10L);
        영호선_강남역_부터_삼성역_params = createParams(영호선, "bg-red-100", stationFixture.get강남역_ID(), stationFixture.get선릉역_ID(), 10L);
        일호선_잠실역_부터_강남역_ID = JsonPathHelper.getObject(
                LineApiCaller.지하철_노선_생성(
                        createParams("일호선", "blue", stationFixture.get잠실역_ID(), stationFixture.get강남역_ID(), 10L)
                ), "id", Long.class);
        이호선_잠실역_부터_삼성역_ID = JsonPathHelper.getObject(
                LineApiCaller.지하철_노선_생성(
                        createParams("이호선", "green", stationFixture.get잠실역_ID(), stationFixture.get삼성역_ID(), 10L)
                ), "id", Long.class);
        삼호선_강남역_부터_선릉역_ID = JsonPathHelper.getObject(
                LineApiCaller.지하철_노선_생성(
                        createParams("삼호선", "orange", stationFixture.get강남역_ID(), stationFixture.get선릉역_ID(), 5L)
                ), "id", Long.class);
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

    public Long get일호선_잠실역_부터_강남역_ID() {
        return 일호선_잠실역_부터_강남역_ID;
    }

    public Long get이호선_잠실역_부터_삼성역_ID() {
        return 이호선_잠실역_부터_삼성역_ID;
    }

    public Long get삼호선_강남역_부터_선릉역_ID() {
        return 삼호선_강남역_부터_선릉역_ID;
    }
}
