package nextstep.fixture;

import java.util.HashMap;
import java.util.Map;

public class LineFixture {

    public static Map<String, String> createLineCreateParams() {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        return lineCreateParams;
    }

    public static Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    public static Map<String, String> createLineUpdateParams(String name, String color) {
        Map<String, String> lineUpdateParams;
        lineUpdateParams = new HashMap<>();
        lineUpdateParams.put("name", name);
        lineUpdateParams.put("color", color);
        return lineUpdateParams;
    }
}
