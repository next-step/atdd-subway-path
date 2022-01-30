package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.HttpRequestTestUtil.*;

public class StationStepUtil {

    public static final String 기본주소 = "/stations";
    public static final String 기존지하철 = "기존지하철";
    public static final String 새로운지하철 = "새로운지하철";
    public static final int 역간_거리 = 10;

    public static final String 지하철_역_아이디_키 = "id";
    public static final String 지하철_역_이름_키 = "name";

    public static ExtractableResponse<Response> 지하철역생성(String 지하철역이름) {
        Map<String, Object> params = 지하철역파라미터생성(지하철역이름);
        return 포스트_요청(기본주소, params);
    }

    public static ExtractableResponse<Response> 지하철역조회(String url) {
        return 겟_요청(url);
    }

    public static ExtractableResponse<Response> 지하철역삭제(String url) {
        return 딜리트_요청(url);
    }

    private static Map<String, Object> 지하철역파라미터생성(String 지하철역) {
        Map<String, Object> params = new HashMap<>();
        params.put(지하철_역_이름_키, 지하철역);
        return params;
    }

}
