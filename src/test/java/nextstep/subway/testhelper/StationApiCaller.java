package nextstep.subway.testhelper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class StationApiCaller {

    public static ExtractableResponse<Response> 지하철_역_생성(Map<String, String> params) {
        return given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역들_조회() {
        return given().log().all()
                .when().get("/stations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_삭제(String location) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }
}
