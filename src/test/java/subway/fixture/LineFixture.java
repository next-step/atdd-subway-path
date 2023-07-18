package subway.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.request.LineModifyRequest;
import subway.dto.request.LineRequest;

public class LineFixture {
    public static ExtractableResponse<Response> createLine(LineRequest dto) {
        return RestAssured.given().log().all()
            .body(dto)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> findLine(Long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/{id}", id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> modifyLine(Long id, LineModifyRequest request) {
        return RestAssured.given()
            .body(request).log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/{id}", id)
            .then().log().all()
            .extract();
    }

}
