package nextstep.subway.acceptance.line;

import static nextstep.subway.acceptance.AcceptanceHelper.statusCodeShouldBe;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import nextstep.subway.applicaion.line.request.LineCreateRequest;
import nextstep.subway.applicaion.line.request.LineUpdateRequest;
import nextstep.subway.applicaion.line.response.LineResponse;

public class LineSteps {

    public static final String BASE_URL = "/lines";

    public static LineResponse 지하철노선을_생성한다(final Long upStationId, final Long downStationId, final int distance) {
        final var request = new LineCreateRequest("신분당선", "bg-red-600", upStationId, downStationId, distance);
        return 지하철노선을_생성한다(request);
    }

    public static LineResponse 지하철노선을_생성한다(final LineCreateRequest request) {
        final var response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(BASE_URL)
                .then();

        statusCodeShouldBe(response, HttpStatus.CREATED);

        return response.extract()
                .jsonPath()
                .getObject("", LineResponse.class);
    }

    public static void 지하철노선을_생성한다(final List<LineCreateRequest> requests) {
        requests.forEach(LineSteps::지하철노선을_생성한다);
    }

    public static List<LineResponse> 모든_지하철노선을_조회한다() {
        final var response = RestAssured.given()
                .when().get(BASE_URL)
                .then();

        statusCodeShouldBe(response, HttpStatus.OK);

        return response.extract()
                .jsonPath()
                .getList("", LineResponse.class);
    }

    public static LineResponse 지하철노선을_조회한다(final Long id) {
        final var response = RestAssured.given()
                .when().get(BASE_URL + "/" + id)
                .then();

        statusCodeShouldBe(response, HttpStatus.OK);

        return response.extract()
                .jsonPath()
                .getObject("", LineResponse.class);
    }

    public static void 지하철노선을_수정한다(final Long id, final LineUpdateRequest request) {
        final var response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put(BASE_URL + "/" + id)
                .then();

        statusCodeShouldBe(response, HttpStatus.OK);
    }

    public static void 지하철노선_수정에_실패한다(final Long id, final LineUpdateRequest request) {
        final var response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put(BASE_URL + "/" + id)
                .then();

        statusCodeShouldBe(response, HttpStatus.BAD_REQUEST);
    }

    public static void 지하철노선을_제거한다(final Long id) {
        final var response = RestAssured.given()
                .when().delete(BASE_URL + "/" + id)
                .then();

        statusCodeShouldBe(response, HttpStatus.NO_CONTENT);
    }
}
