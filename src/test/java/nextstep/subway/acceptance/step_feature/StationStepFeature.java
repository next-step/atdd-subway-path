package nextstep.subway.acceptance.step_feature;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationStepFeature {

    public static final String 강남역_이름 = "강남역";
    public static final String 판교역_이름 = "판교역";
    public static final String 정자역_이름 = "정자역";
    public static final String 미금역_이름 = "미금역";
    private static final String CREATE_STATION_NAME_PARAM_KEY = "name";
    private static final String STATION_BASE_URI = "stations";

    public static StationResponse 지하철역_생성_조회_요청(String name) {
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(name);
        String location = createResponse.header("Location");

        ExtractableResponse<Response> response = 지하쳘역_삭제_요청(location);
        return response.as(StationResponse.class);

    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = createStationParams(name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(STATION_BASE_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 모든_지하철역_조회_요청() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(STATION_BASE_URI)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하쳘역_삭제_요청(String uri) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(String uri) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
        return response;
    }

    public static void checkCreateStation(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void checkCreateStationFail(ExtractableResponse<Response> response) {
        checkResponseStatus(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    public static void checkFindStation(ExtractableResponse<Response> response) {
        checkResponseStatus(response.statusCode(), HttpStatus.OK);
    }

    public static void checkResponseStatus(int statusCode, HttpStatus httpStatus) {
        assertThat(statusCode).isEqualTo(httpStatus.value());
    }

    public static void 역_조회_검증(ExtractableResponse<Response> response, List<String> names) {
        String stationName = response.jsonPath().getString("name");
        assertThat(stationName).contains(names);
    }

    private static Map<String, String> createStationParams(String name) {
        Map<String, String> params = new HashMap();
        params.put(CREATE_STATION_NAME_PARAM_KEY, name);

        return params;
    }

}
