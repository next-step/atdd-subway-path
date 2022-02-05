package nextstep.subway.acceptance.model;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public final class StationEntitiesHelper {

    private static final String REQUEST_URI = "/stations";
    public static final String 서초역 = "서초역";
    public static final String 교대역 = "교대역";
    public static final String 강남역 = "강남역";
    public static final String 역삼역 = "역삼역";
    public static final String 선릉역 = "선릉역";
    public static final String 판교역 = "판교역";
    public static final String 정자역 = "정자역";
    public static final String 삼성역 = "삼성역";
    public static final String 남부터미널역 = "남부터미널역";
    public static final String 양재역 = "양재역";

    public static Long 지하철역_생성_요청후_아이디_조회(String name) {
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(name);
        return createResponse.jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        return RestAssured.given().log().all()
                .body(newStation(name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(REQUEST_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get(REQUEST_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private static Map<String, String> newStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return params;
    }
}
