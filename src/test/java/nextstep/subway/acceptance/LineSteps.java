package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineSteps {

    public static ExtractableResponse<Response> createLine(String name, String color, Long upStationId, Long downStationId, Long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static List<String> getLineNames() {
        return getLines().jsonPath().getList("name", String.class);
    }

    public static ExtractableResponse<Response> getLine(String locationHeader) {
        return RestAssured.given().log().all()
                .when().get(locationHeader)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static List<Long> getLineStationIds(String locationHeader) {
        return getLine(locationHeader)
                .jsonPath()
                .getList("stations", StationResponse.class)
                .stream()
                .map(station -> station.getId())
                .collect(Collectors.toList());
    }

    public static void updateLine(String name, String color, String locationHeader) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().patch(locationHeader)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static void deleteLine(String locationHeader) {
        RestAssured.given().log().all()
                .when().delete(locationHeader)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

}
