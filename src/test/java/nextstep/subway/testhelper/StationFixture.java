package nextstep.subway.testhelper;

import java.util.HashMap;
import java.util.Map;

public class StationFixture {
    public static final String 잠실역 = "잠실역";
    public static final String 강남역 = "강남역";
    public static final String 삼성역 = "삼성역";
    public static final String 선릉역 = "선릉역";
    public static final String 교대역 = "교대역";
    public static final String 서초역 = "서초역";
    private Map<String, String> 잠실역_params;
    private Map<String, String> 강남역_params;
    private Map<String, String> 삼성역_params;
    private Map<String, String> 선릉역_params;
    private Map<String, String> 교대역_params;
    private Map<String, String> 서초역_params;
    private Long 잠실역_ID;
    private Long 강남역_ID;
    private Long 삼성역_ID;
    private Long 선릉역_ID;
    private Long 교대역_ID;
    private Long 서초역_ID;

    public StationFixture() {
        Map<String, String> params = new HashMap<>();
        params.put("name", 잠실역);
        잠실역_params = params;
        잠실역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(잠실역_params), "id", Long.class);

        params.put("name", 강남역);
        강남역_params = params;
        강남역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(강남역_params), "id", Long.class);

        params.put("name", 삼성역);
        삼성역_params = params;
        삼성역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(삼성역_params), "id", Long.class);

        params.put("name", 선릉역);
        선릉역_params = params;
        선릉역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(선릉역_params), "id", Long.class);

        params.put("name", 교대역);
        교대역_params = params;
        교대역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(교대역_params), "id", Long.class);

        params.put("name", 서초역);
        서초역_params = params;
        서초역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(서초역_params), "id", Long.class);
    }

    public Map<String, String> get잠실역_params() {
        return 잠실역_params;
    }

    public Map<String, String> get강남역_params() {
        return 강남역_params;
    }

    public Map<String, String> get삼성역_params() {
        return 삼성역_params;
    }

    public Map<String, String> get선릉역_params() {
        return 선릉역_params;
    }

    public Map<String, String> get교대역_params() {
        return 교대역_params;
    }

    public Map<String, String> get서초역_params() {
        return 서초역_params;
    }

    public Long get잠실역_ID() {
        return 잠실역_ID;
    }

    public Long get강남역_ID() {
        return 강남역_ID;
    }

    public Long get삼성역_ID() {
        return 삼성역_ID;
    }

    public Long get선릉역_ID() {
        return 선릉역_ID;
    }

    public Long get교대역_ID() {
        return 교대역_ID;
    }

    public Long get서초역_ID() {
        return 서초역_ID;
    }
}
