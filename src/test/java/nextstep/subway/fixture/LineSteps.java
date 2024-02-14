package nextstep.subway.fixture;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.line.LineResponse;

import java.util.List;
import java.util.Map;

public class LineSteps {
    public static LineResponse createLine(Map<String, Object> param) {
        return RestAssured.given().log().all()
                .when()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(LineResponse.class);
    }

    public static List<LineResponse> getLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<List<LineResponse>>() {});
    }


    public static LineResponse getLine(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when()
                .get("/lines/{id}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(LineResponse.class);
    }
}
