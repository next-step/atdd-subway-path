package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionSteps {

    private static final String PATH = "/sections";

    public static ExtractableResponse<Response> 구간_등록_요청(String lineUri, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(ContentType.JSON)
                .when()
                .post(lineUri + PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 구간_삭제_요청(String lineUri, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .delete(lineUri + PATH + "?stationId=" + stationId)
                .then().log().all()
                .extract();
    }

    public static void 구간_등록_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 구간_삭제_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
