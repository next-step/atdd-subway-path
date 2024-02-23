package nextstep.subway.support.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

public class StationSteps {

    public static final String STATION_BASE_PATH = "/stations";
    public static final String STATION_RESOURCE_PATH = STATION_BASE_PATH + "/{stationId}";
    public static final String ID = "id";
    public static final String NAME = "name";

    public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(STATION_BASE_PATH)
            .then().log().all()
            .extract();
    }


    public static ExtractableResponse<Response> 지하철_역_생성_요청(Map<String, Object> body) {
        return RestAssured.given()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(STATION_BASE_PATH)
            .then()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_삭제_요청(Long 지하철역_아이디) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(STATION_RESOURCE_PATH, 지하철역_아이디)
            .then().log().all()
            .extract();
    }


    public static Long 지하철역_응답에서_역_아이디_추출(ExtractableResponse<Response> 지하철역_응답) {
        return 지하철역_응답.jsonPath().getLong(ID);
    }

    public static List<String> 지하철역_목록_응답에서_역_이름_목록_추출(ExtractableResponse<Response> 지하철역_응답) {
        return 지하철역_응답.jsonPath().getList(NAME, String.class);
    }
}
