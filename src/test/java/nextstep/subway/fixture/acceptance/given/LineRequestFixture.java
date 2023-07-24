package nextstep.subway.fixture.acceptance.given;

import java.util.HashMap;
import java.util.Map;

public abstract class LineRequestFixture {

    public final static String 신분당선 = "신분당선";
    public final static String 분당선 = "분당선";
    public static final String 다른분당선 = "다른분당선";
    public final static String red = "bg-red-600";
    public final static String green = "bg-green-600";
    public final static long 지하철역_id = 1;
    public final static long 새로운지하철역_id = 2;
    public final static long 또다른지하철역_id = 3;
    public final static int distance = 10;

    public static Map<String, Object> 노선등록요청_데이터_생성(String name, String color, long upStationId, long downStationId, int distance) {

        Map<String, Object> params = new HashMap<>();

        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return params;
    }
}
