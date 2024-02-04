package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.controller.dto.LineCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineApiRequester {

    /** 주어진 지하철 노선 정보를 통해 지하철 노선 생성 요청 후 응답값을 반환합니다 */
    public static ExtractableResponse<Response> createLine(
        final LineCreateRequest request
    ) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract();
    }

    /** 모든 지하철 노선을 조회하는 요청 후 응답값을 반환합니다 */
    public static ExtractableResponse<Response> getAllLines() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    /** 주어진 지하철 노선 식별자를 통해 지하철 노선 조회 후 응답값을 반환합니다 */
    public static ExtractableResponse<Response> getLineById(final long id) {
        return RestAssured.given().log().all()
            .pathParam("id", id)
            .when().get("/lines/{id}")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }
}
