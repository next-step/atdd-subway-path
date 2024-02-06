package nextstep.subway.testhelper;

import java.util.HashMap;
import java.util.Map;

public class LineFixture {
    public static final String 신분당선 = "신분당선";
    public static final String 영호선 = "0호선";
    private Map<String, String> 신분당선_강남역_부터_삼성역_params;
    private Map<String, String> 영호선_강남역_부터_삼성역_params;

    public LineFixture(StationFixture stationFixture) {
        신분당선_강남역_부터_삼성역_params = new HashMap<>();
        신분당선_강남역_부터_삼성역_params.put("name", 신분당선);
        신분당선_강남역_부터_삼성역_params.put("color", "bg-red-600");
        신분당선_강남역_부터_삼성역_params.put("upStationId", stationFixture.get강남역_ID().toString());
        신분당선_강남역_부터_삼성역_params.put("downStationId", stationFixture.get삼성역_ID().toString());
        신분당선_강남역_부터_삼성역_params.put("distance", "10");

        영호선_강남역_부터_삼성역_params = new HashMap<>();
        영호선_강남역_부터_삼성역_params.put("name", 영호선);
        영호선_강남역_부터_삼성역_params.put("color", "bg-red-100");
        영호선_강남역_부터_삼성역_params.put("upStationId", stationFixture.get강남역_ID().toString());
        영호선_강남역_부터_삼성역_params.put("downStationId", stationFixture.get선릉역_ID().toString());
        영호선_강남역_부터_삼성역_params.put("distance", "10");
    }

    public Map<String, String> get신분당선_강남역_부터_삼성역_params() {
        return 신분당선_강남역_부터_삼성역_params;
    }

    public Map<String, String> get영호선_강남역_부터_삼성역_params() {
        return 영호선_강남역_부터_삼성역_params;
    }
}
