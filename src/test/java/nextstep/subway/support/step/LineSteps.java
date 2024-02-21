package nextstep.subway.support.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

public class LineSteps {


    public static final String LINE_BASE_PATH = "/lines";
    public static final String LINE_RESOURCE_PATH = LINE_BASE_PATH + "/{lineId}";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String COLOR = "color";

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, Object> body) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(LINE_BASE_PATH)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(LINE_BASE_PATH)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_단일_조회_요청(Long 노선_아이디) {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(LINE_RESOURCE_PATH, 노선_아이디)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long 노선_아이디, Map<String, Object> body) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(LINE_RESOURCE_PATH, 노선_아이디)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(Long 노선_아이디) {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(LINE_RESOURCE_PATH, 노선_아이디)
            .then().log().all()
            .extract();
    }

    public static List<String> 지하철_노선_목록_응답에서_노선_이름_목록_추출(ExtractableResponse<Response> 지하철_노선_목록_응답) {
        return 지하철_노선_목록_응답.jsonPath()
            .getList(NAME, String.class);
    }

    public static List<Long> 지하철_노선_목록_응답에서_노선_아이디_목록_추출(ExtractableResponse<Response> 지하철_노선_목록_응답) {
        return 지하철_노선_목록_응답.jsonPath()
            .getList(ID, Long.class);
    }


    public static Long 지하철_노선_응답에서_노선_아이디_추출(ExtractableResponse<Response> 지하철_노선_응답) {
        return 지하철_노선_응답.jsonPath().getLong(ID);
    }

    public static String 지하철_노선_응답에서_노선_이름_추출(ExtractableResponse<Response> 지하철_노선_응답) {
        return 지하철_노선_응답.jsonPath().get(NAME);
    }

    public static String 지하철_노선_응답에서_노선_색상_추출(ExtractableResponse<Response> 지하철_노선_응답) {
        return 지하철_노선_응답.jsonPath().get(COLOR);
    }

    public static Long 지하철_노선_응답에서_노선의_상행_종점역_아이디_추출(ExtractableResponse<Response> 지하철_노선_응답) {
        return 지하철_노선_응답에서_역_아이디_추출(지하철_노선_응답, 0);
    }


    public static Long 지하철_노선_응답에서_노선의_하행_종점역_아이디_추출(ExtractableResponse<Response> 지하철_노선_응답) {
        return 지하철_노선_응답에서_역_아이디_추출(지하철_노선_응답, -1);
    }

    public static List<Long> 지하철_노선_응답에서_역_아이디_목록_추출(ExtractableResponse<Response> 지하철_노선_응답) {
        return 지하철_노선_응답.jsonPath().getList("stations.id", Long.class);
    }

    public static long 지하철_노선_응답에서_역_아이디_추출(ExtractableResponse<Response> 지하철_노선_응답, long idx) {
        return 지하철_노선_응답.jsonPath().getLong(String.format("stations[%d].id", idx));
    }

}
