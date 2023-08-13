package nextstep.subway.acceptance.section;

import io.restassured.RestAssured;
import nextstep.subway.linesection.LineSectionRequest;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineSectionFixture {
    public static void 지하철_구간_삭제(Long lineId, Long stationId) {
        RestAssured.given().log().all()
                .when()
                .param("stationId", stationId)
                .delete(String.format("/lines/%d/sections", lineId))
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_구간_삭제_응답_상태값_체크(Long lineId, Long stationId, HttpStatus expected) {
        RestAssured.given().log().all()
                .when()
                .param("stationId", stationId)
                .delete(String.format("/lines/%d/sections", lineId))
                .then().log().all()
                .statusCode(expected.value());
    }

    public static void 지하철_구간_생성_응답_상태값_체크(Long lineId, LineSectionRequest request, HttpStatus expected) {
        RestAssured.given().log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post(String.format("/lines/%d/sections", lineId))
                .then().log().all()
                .statusCode(expected.value());
    }

    public static void 지하철_구간_생성(Long lineId, LineSectionRequest request) {
        RestAssured.given().log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post(String.format("/lines/%d/sections", lineId))
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    public static LineSectionRequest 구간_생성_요청서(Long startStationId, Long endStationId) {
        LineSectionRequest request = new LineSectionRequest(startStationId, endStationId, RandomUtils.nextInt(1, 10));
        return request;
    }

    public static LineSectionRequest 구간_생성_요청서(Long startStationId, Long endStationId, Integer distance) {
        LineSectionRequest request = new LineSectionRequest(startStationId, endStationId, distance);
        return request;
    }
}
