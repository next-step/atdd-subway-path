package nextstep.subway.acceptance.step_feature;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PathServiceStepFeature {

    public static PathResponse 최단경로_조회_요청_응답(long sourceId, long targetId) {
        return 최단경로_조회_요청(sourceId, targetId)
                .as(PathResponse.class);
    }

    public static ExtractableResponse<Response> 최단경로_조회_요청(long sourceId, long targetId) {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
                .then()
                .log()
                .all()
                .extract();
    }

    public static void 최단경로_조회_응답상태_검증(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    public static void 최단경로_조회_응답_검증(PathResponse response, List<String> stationNames) {
        List<String> names = response.getStations()
                .stream()
                .map(it -> it.getName())
                .collect(Collectors.toList());

        assertThat(names).isEqualTo(stationNames);
    }

}
