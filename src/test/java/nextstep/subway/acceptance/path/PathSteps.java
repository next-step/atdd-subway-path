package nextstep.subway.acceptance.path;

import static nextstep.subway.acceptance.AcceptanceHelper.statusCodeShouldBe;

import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import nextstep.subway.applicaion.path.response.PathResponse;

public class PathSteps {

    public static final String BASE_URL = "/paths";

    public static PathResponse 최단경로를_조회한다(final Long sourceId, final Long targetId) {
        final var response = RestAssured.given()
                .when().get(String.format("%s?source=%d&target=%d", BASE_URL, sourceId, targetId))
                .then();

        statusCodeShouldBe(response, HttpStatus.OK);

        return response.extract().as(PathResponse.class);
    }

    public static void 최단경로_조회에_실패한다(final Long sourceId, final Long targetId) {
        final var response = RestAssured.given()
                .when().get(String.format("%s?source=%d&target=%d", BASE_URL, sourceId, targetId))
                .then();

        statusCodeShouldBe(response, HttpStatus.BAD_REQUEST);
    }
}
