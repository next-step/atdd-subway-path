package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.HttpRequestTestUtil.딜리트_요청;
import static nextstep.subway.utils.HttpRequestTestUtil.포스트_요청;

public class SectionStepUtil {

    private static final String 기본구간주소 = "/lines/1/sections";
    private static final String 쿼리스트링 = "/?stationId=";

    private SectionStepUtil() {
    }


    public static ExtractableResponse<Response> 구간삭제요청(Long 하행종점) {
        return 딜리트_요청(기본구간주소 + 쿼리스트링 + 하행종점);
    }


    public static ExtractableResponse<Response> 구간등록(Long 상행종점, Long 하행종점, int 종점간거리) {
        Map<String, Object> 구간파라미터생성 = 구간파라미터생성(상행종점, 하행종점, 종점간거리);

        return 포스트_요청(기본구간주소, 구간파라미터생성);
    }

    private static Map<String, Object> 구간파라미터생성(Long 상행종점, Long 하행종점, int 종점간거리) {
        Map<String, Object> params = new HashMap<>();
        params.put(LineStepUtil.노선_상행역_키, 상행종점);
        params.put(LineStepUtil.노선_하행역_키, 하행종점);
        params.put(LineStepUtil.노선_역간_거리_키, 종점간거리);
        return params;
    }


}
