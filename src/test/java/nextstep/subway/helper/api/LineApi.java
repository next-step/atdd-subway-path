package nextstep.subway.helper.api;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.controller.dto.LineCreateRequestBody;
import nextstep.subway.controller.dto.LineUpdateRequestBody;
import nextstep.subway.controller.dto.SectionCreateRequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineApi {
    private static final String routePrefix = "/lines";

    public static JsonPath 노선생성요청(LineCreateRequestBody requestBody) {
        return RestAssured.given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(routePrefix)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().jsonPath();
    }

    public static ExtractableResponse<Response> 구간생성요청(Long lineId, SectionCreateRequestBody requestBody) {
        return RestAssured.given().log().all()
                .body(requestBody)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(routePrefix + "/{id}/sections")
                .then().log().all().extract();
    }

    public static JsonPath 노선목록조회요청() {
        return RestAssured.given().log().all()
                .when().get(routePrefix)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }

    public static JsonPath 노선조회요청(Long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().get(routePrefix + "/{id}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }

    public static JsonPath 구간조회요청(Long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().get(routePrefix + "/{id}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }

    public static ExtractableResponse<Response> 노선수정요청(Long lineId, LineUpdateRequestBody requestBody) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(routePrefix + "/{id}")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 노선삭제요청(Long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().delete(routePrefix + "/{id}")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 구간삭제요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .queryParam("stationId", stationId)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(routePrefix + "/{id}/sections")
                .then().log().all().extract();
    }
}
