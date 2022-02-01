package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineSteps {
    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        return RestAssured
                .given().log().all()
                .when().get(createResponse.header("location"))
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(노선_생성_파라미터 params) {
        return RestAssured
                .given().log().all()
                .body(params.createLineCreateParams())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static Long 아이디_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(Long lineId, 구간_생성_파라미터 params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params.createSectionCreateParams())
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철역_경로_검색_요청(Long sourceStationId, Long targetStationId) {
        return RestAssured.given().log().all()
                .when().get("/path?source={sourceStationId}&target={targetStationId}", sourceStationId, targetStationId)
                .then().log().all().extract();
    }

    public static List<Long> 역_아이디_목록_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Long.class);
    }

    public static int 최단_경로_거리_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getInt("distance");
    }

    static class 노선_생성_파라미터 {

        String name;
        String color;
        long upStationId;
        long downStationId;
        int distance;

        public 노선_생성_파라미터(Long 상행역, Long 하행역, int 상행역_하행역_거리) {
            name = "신분당선";
            color = "bg-red-600";
            upStationId = 상행역;
            downStationId = 하행역;
            distance = 상행역_하행역_거리;
        }

        public 노선_생성_파라미터(String 노선이름, String 노선색상, Long 상행역, Long 하행역, int 상행역_하행역_거리) {
            name = 노선이름;
            color = 노선색상;
            upStationId = 상행역;
            downStationId = 하행역;
            distance = 상행역_하행역_거리;
        }

        private Map<String, String> createLineCreateParams() {
            Map<String, String> lineCreateParams;
            lineCreateParams = new HashMap<>();
            lineCreateParams.put("name", name);
            lineCreateParams.put("color", color);
            lineCreateParams.put("upStationId", String.valueOf(upStationId));
            lineCreateParams.put("downStationId", String.valueOf(downStationId));
            lineCreateParams.put("distance", String.valueOf(distance));
            return lineCreateParams;
        }

    }

    static class 구간_생성_파라미터 {
        static final int DEFAULT_DISTANCE = 5;
        long upStationId;
        long downStationId;
        int distance;

        public 구간_생성_파라미터(Long 상행역, Long 하행역) {
            this(상행역, 하행역, DEFAULT_DISTANCE);
        }

        public 구간_생성_파라미터(Long 상행역, Long 하행역, int 상행역_하행역_거리) {
            upStationId = 상행역;
            downStationId = 하행역;
            distance = 상행역_하행역_거리;
        }

        private Map<String, String> createSectionCreateParams() {
            Map<String, String> params = new HashMap<>();
            params.put("upStationId", String.valueOf(upStationId));
            params.put("downStationId", String.valueOf(downStationId));
            params.put("distance", String.valueOf(distance));
            return params;
        }
    }

}
