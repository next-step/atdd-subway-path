package nextstep.subway.testhelper;

import nextstep.subway.station.Station;

import java.util.HashMap;
import java.util.Map;

public class StationFixture {
    public static final Station 잠실역 = new Station(1L, "잠실역");
    public static final Station 강남역 = new Station(2L, "강남역");
    public static final Station 삼성역 = new Station(3L, "삼성역");
    public static final Station 선릉역 = new Station(4L, "선릉역");
    public static final Station 교대역 = new Station(5L, "교대역");
    public static final Station 서초역 = new Station(6L, "서초역");
    public static final Station 오이도역 = new Station(7L, "오이도역");
    private Map<String, String> 잠실역_params;
    private Map<String, String> 강남역_params;
    private Map<String, String> 삼성역_params;
    private Map<String, String> 선릉역_params;
    private Map<String, String> 교대역_params;
    private Map<String, String> 서초역_params;
    private Map<String, String> 오이도역_params;
    private Long 잠실역_ID;
    private Long 강남역_ID;
    private Long 삼성역_ID;
    private Long 선릉역_ID;
    private Long 교대역_ID;
    private Long 서초역_ID;
    private Long 오이도역_ID;

    public StationFixture() {
        Map<String, String> params = new HashMap<>();
        params.put("name", 잠실역.getName());
        잠실역_params = params;
        잠실역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(잠실역_params), "id", Long.class);

        params.put("name", 강남역.getName());
        강남역_params = params;
        강남역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(강남역_params), "id", Long.class);

        params.put("name", 삼성역.getName());
        삼성역_params = params;
        삼성역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(삼성역_params), "id", Long.class);

        params.put("name", 선릉역.getName());
        선릉역_params = params;
        선릉역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(선릉역_params), "id", Long.class);

        params.put("name", 교대역.getName());
        교대역_params = params;
        교대역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(교대역_params), "id", Long.class);

        params.put("name", 서초역.getName());
        서초역_params = params;
        서초역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(서초역_params), "id", Long.class);

        params.put("name", 오이도역.getName());
        오이도역_params = params;
        오이도역_ID = JsonPathHelper.getObject(StationApiCaller.지하철_역_생성(오이도역_params), "id", Long.class);
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

    public Map<String, String> get오이도역_params() {
        return 오이도역_params;
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

    public Long get오이도역_ID() {
        return 오이도역_ID;
    }
}
