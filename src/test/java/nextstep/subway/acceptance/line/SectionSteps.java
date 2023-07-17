package nextstep.subway.acceptance.line;

import static nextstep.subway.acceptance.AcceptanceHelper.asExceptionResponse;
import static nextstep.subway.acceptance.AcceptanceHelper.statusCodeShouldBe;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import nextstep.subway.applicaion.line.request.SectionRequest;
import nextstep.subway.applicaion.line.response.LineResponse;
import nextstep.subway.ui.ExceptionResponse;

public class SectionSteps {

    public static final String BASE_URL = "/lines";

    public static LineResponse 지하철구간을_등록한다(
            final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        return 지하철구간을_등록한다(lineId, new SectionRequest(upStationId, downStationId, distance));
    }

    public static LineResponse 지하철구간을_등록한다(final Long lineId, final SectionRequest request) {
        final var response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(BASE_URL + "/" + lineId + "/sections")
                .then();

        statusCodeShouldBe(response, HttpStatus.OK);

        return response.extract()
                .jsonPath()
                .getObject("", LineResponse.class);
    }

    public static ExceptionResponse 지하철구간_등록에_실패한다(final Long lineId, final SectionRequest request) {
        final var response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(BASE_URL + "/" + lineId + "/sections")
                .then();

        return asExceptionResponse(response);
    }

    public static void 지하철구간을_제거한다(final Long lineId, final Long stationId) {
        final var response = RestAssured.given()
                .param("stationId", stationId)
                .when().delete(BASE_URL + "/" + lineId + "/sections")
                .then();

        statusCodeShouldBe(response, HttpStatus.NO_CONTENT);
    }

    public static void 지하철구간_제거에_실패한다(final Long lineId, final Long stationId) {
        final var response = RestAssured.given()
                .param("stationId", stationId)
                .when().delete(BASE_URL + "/" + lineId + "/sections")
                .then();

        statusCodeShouldBe(response, HttpStatus.BAD_REQUEST);
    }
}
