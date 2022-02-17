package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationSteps {

    public static final String STATIONS_PATH = "/stations";

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(STATIONS_PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(STATIONS_PATH)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(ExtractableResponse<Response> createResponse) {
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
        return response;
    }

    public static Long 지하철역_생성(String name) {
        return 지하철역_생성_요청(name).jsonPath().getLong("id");
    }

    public static void 지하철역_생성됨(ExtractableResponse<Response> response) {
        assertStatus(response ,HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철역_목록_조회됨(ExtractableResponse<Response> response, String ... names) {
        assertStatus(response, HttpStatus.OK);
        List<String> stationNames = response.jsonPath().getList("name");
        assertThat(stationNames).contains(names);
    }

    public static void 지하철역_삭제됨(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.NO_CONTENT);
    }

    public static void 지하철역_생성_중복됨(ExtractableResponse<Response> createResponse) {
        assertStatus(createResponse, HttpStatus.BAD_REQUEST);
    }

    private static void assertStatus(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
