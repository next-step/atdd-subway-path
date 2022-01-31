package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.HttpRequestTestUtil.*;

public class LineStepUtil {

    public static final String 기본주소 = "/lines";
    public static final String 기존노선 = "기존노선";
    public static final String 기존색상 = "기존색상";
    public static final String 새로운노선 = "새로운노선";
    public static final String 새로운색상 = "새로운색상";
    public static final String 수정노선 = "수정 노선";
    public static final String 수정색상 = "수정 색상";

    public static final String 노선_이름_키 = "name";
    public static final String 노선_색상_키 = "color";
    public static final String 노선_상행역_키 = "upStationId";
    public static final String 노선_하행역_키 = "downStationId";
    public static final String 노선_역간_거리_키 = "distance";

    private LineStepUtil() {
    }

    public static ExtractableResponse<Response> 노선조회(String url) {
        return 겟_요청(url);
    }

    public static ExtractableResponse<Response> 노선삭제(String url) {
        return 딜리트_요청(url);
    }

    public static ExtractableResponse<Response> 노선생성(Map<String, Object> param) {
        return 포스트_요청(기본주소, param);
    }

    public static ExtractableResponse<Response> 노선수정(ExtractableResponse<Response> createResponse) {
        Map<String, Object> updateParam = 노선파라미터생성(수정노선, 수정색상, null, null, 0);
        ExtractableResponse<Response> updateResponse = 풋_요청(createResponse.header(HttpHeaders.LOCATION), updateParam);
        return updateResponse;
    }

    public static Map<String, Object> 노선파라미터생성(String 노선, String 색상, Long 상행종점, Long 하행종점, int 종점간거리) {
        Map<String, Object> param = new HashMap<>();
        param.put(노선_이름_키, 노선);
        param.put(노선_색상_키, 색상);
        param.put(노선_상행역_키, 상행종점);
        param.put(노선_하행역_키, 하행종점);
        param.put(노선_역간_거리_키, 종점간거리);
        return param;
    }
}
