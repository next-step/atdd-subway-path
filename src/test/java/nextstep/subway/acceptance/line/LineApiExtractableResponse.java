package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.presentation.request.CreateLineRequest;
import nextstep.subway.line.presentation.request.UpdateLineRequest;
import org.springframework.http.MediaType;

public class LineApiExtractableResponse {

    public static ExtractableResponse<Response> 지하철_노선_생성(CreateLineRequest createLineRequest) {
        return RestAssured
                .given().log().all()
                .body(createLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long lineId) {
        return RestAssured
                .given().log().all()
                .pathParam("id", lineId)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(Long lineId, UpdateLineRequest updateLineRequest) {
        return RestAssured
                .given().log().all()
                .body(updateLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineId)
                .when().put("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(Long lineId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineId)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }

}
