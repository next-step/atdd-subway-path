package nextstep.subway.utils.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * 지하철역 관리자 유틸 클래스
 */
public class StationManager {

    public static ExtractableResponse<Response> findAll() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> save(String name) {
        Map<String, String> param = new HashMap<>();
        param.put("name", name);

        return RestAssured.given()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static void delete(long id) {
        RestAssured.given().log().all()
                .when().delete("/stations/{id}", id)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
