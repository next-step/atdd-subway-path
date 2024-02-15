package nextstep.subway.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.presentation.request.LineCreateRequest;
import nextstep.subway.line.presentation.request.LineUpdateRequest;
import org.springframework.http.MediaType;

public class LineSteps {

    public static Long createLine(LineCreateRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract()
                .response().jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> getLines() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> getLine(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> putLine(Long lineId, LineUpdateRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteLine(Long lineId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + lineId)
                .then().log().all()
                .extract();
    }
}
