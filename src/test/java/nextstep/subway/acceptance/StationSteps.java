package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationSteps {
    private static final String PATH = "/stations";

    public static ExtractableResponse<Response> 역_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(ContentType.JSON)
                .when()
                .post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 역_목록_조회_요청() {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when()
                .get(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 역_삭제_요청(String uri) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 역_생성_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 역_목록_조회_완료(ExtractableResponse<Response> response, Map<String, String>... paramsArgs) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stationNames = response.jsonPath().getList("name");
        assertThat(stationNames).contains(Arrays.stream(paramsArgs).map(m -> m.get("name")).toArray(String[]::new));
    }

    public static void 역_삭제_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 중복된_역_생성_예외(ExtractableResponse<Response> response){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
