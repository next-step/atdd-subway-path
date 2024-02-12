package nextstep.subway.utils.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.line.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * 지하철역 노선 관리자 유틸 클래스
 */
public class StationLineManager {

    public static ExtractableResponse<Response> findById(long lineId) {
        return RestAssured
                .given()
                .when().get("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findAll() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> save(LineRequest lineRequest) {
        return RestAssured
                .given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static void update(long lineId, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", lineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    public static void delete(long lineId) {
        RestAssured
                .given().log().all()
                .when().delete("/lines/{id}", lineId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
