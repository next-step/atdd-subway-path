package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PathSteps {

    private static final String PATH = "/paths";

    public static ExtractableResponse<Response> 최단_경로_찾기_요청(Long sourceId, Long targetId) {
        return RestAssured.given().log().all()
                .param("source", sourceId)
                .param("target", targetId)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when()
                .get(PATH)
                .then().log().all()
                .extract();
    }

    public static void 최단_거리_요청_완료(ExtractableResponse<Response> response, Long... stationIds) {
        var stationIdList = response.jsonPath().getList("stations.id", Long.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stationIdList).containsExactlyInAnyOrder(stationIds)
        );
    }

    public static void 최단_거리_요청_예외(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
