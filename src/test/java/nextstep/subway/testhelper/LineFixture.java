package nextstep.subway.testhelper;

import java.util.HashMap;
import java.util.Map;

public class LineFixture {
    private Map<String, String> 신분당선_params;
    private Map<String, String> 영호선_params;

    public LineFixture(StationFixture stationFixture) {
        신분당선_params = new HashMap<>();
        신분당선_params.put("name", "신분당선");
        신분당선_params.put("color", "bg-red-600");
        신분당선_params.put("upStationId", stationFixture.get강남역_ID().toString());
        신분당선_params.put("downStationId", stationFixture.get삼성역_ID().toString());
        신분당선_params.put("distance", "10");

        영호선_params = new HashMap<>();
        영호선_params.put("name", "0호선");
        영호선_params.put("color", "bg-red-100");
        영호선_params.put("upStationId", stationFixture.get강남역_ID().toString());
        영호선_params.put("downStationId", stationFixture.get선릉역_ID().toString());
        영호선_params.put("distance", "10");
    }

    public Map<String, String> get신분당선_params() {
        return 신분당선_params;
    }

    public Map<String, String> get영호선_params() {
        return 영호선_params;
    }
}
