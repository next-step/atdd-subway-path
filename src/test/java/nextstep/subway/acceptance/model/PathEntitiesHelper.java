package nextstep.subway.acceptance.model;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("경로 조회 기능")
public final class PathEntitiesHelper {

    public static ExtractableResponse<Response> 최단_경로_조회_요청(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

    }

    public static void 최단_경로_조회됨(ExtractableResponse<Response> response, int expectedSize) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.jsonPath().getList("stations")).hasSize(expectedSize);
    }
}
