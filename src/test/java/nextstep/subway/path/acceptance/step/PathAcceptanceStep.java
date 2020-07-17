package nextstep.subway.path.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.path.dto.PathType.DISTANCE;
import static nextstep.subway.path.dto.PathType.DURATION;
import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceStep {
    public static ExtractableResponse<Response> 출발역에서_도착역까지의_최단_거리_경로_조회_요청(Long startStationId, Long endStationId) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .param("source", startStationId)
                .param("target", endStationId)
                .param("type", DISTANCE)
                .get("/paths")
                .then()
                .log().all()
                .extract();
    }


    public static void 최단_거리_경로를_응답함(ExtractableResponse<Response> response, List<Long> expectedPath) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getStations())
                .extracting(StationResponse::getId)
                .containsExactlyElementsOf(expectedPath);
    }

    public static void 총_거리와_소요_시간을_함께_응답함(ExtractableResponse<Response> response) {
        PathResponse pathResponse = response.as(PathResponse.class);

        assertThat(pathResponse.getDistance()).isNotNull();
        assertThat(pathResponse.getDuration()).isNotNull();
    }

    public static ExtractableResponse<Response> 출발역에서_도착역까지의_최소_시간_경로_조회_요청(Long startStationId, Long endStationId) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .param("source", startStationId)
                .param("target", endStationId)
                .param("type", DURATION)
                .get("/paths")
                .then()
                .log().all()
                .extract();
    }


    public static void 최단_시간_경로를_응답함(ExtractableResponse<Response> response, List<Long> expectedPath) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getStations())
                .extracting(StationResponse::getId)
                .containsExactlyElementsOf(expectedPath);
    }


}
