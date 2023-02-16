package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.applicaion.dto.StationResponse;
import org.springframework.http.HttpStatus;

public class StationAcceptanceAssert {

    protected static void 지하철역_생성_요청_검증(final ExtractableResponse<Response> response, String stationName) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        List<String> stationNames =
                RestAssured.given().log().all()
                        .when().get("/stations")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);

        assertThat(stationNames).containsAnyOf(stationName);
    }

    public static void 지하철역_조회_요청_검증(final ExtractableResponse<Response> stationResponse, final int size) {
        List<StationResponse> stations = stationResponse.jsonPath().getList(".", StationResponse.class);

        assertThat(stations).hasSize(size);
    }

    public static void 지하철역_제거_요청_검증(final String stationName) {
        List<String> stationNames =
                RestAssured.given().log().all()
                        .when().get("/stations")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);

        assertThat(stationNames).doesNotContain(stationName);
    }
}
