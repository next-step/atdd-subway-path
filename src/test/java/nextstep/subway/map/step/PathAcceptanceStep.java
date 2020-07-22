package nextstep.subway.map.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.map.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceStep {
    public static ExtractableResponse<Response> 최단_거리_경로_조회_요청(Long source, Long target) {
        Map<String, Long> params = new HashMap<>();

        params.put("source", source);
        params.put("target", target);

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                queryParams(params).
                get("/paths").
                then().
                log().all().
                extract();
    }

    public static void 최단_거리_경로_응답됨(ExtractableResponse<Response> response) {
        PathResponse path = response.as(PathResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(path.getStations().size()).isNotEqualTo(0);
    }

    public static ExtractableResponse<Response> 최소_시간_경로_조회_요청(Long source, Long target) {
        Map<String, Long> params = new HashMap<>();

        params.put("source", source);
        params.put("target", target);

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                queryParams(params).
                queryParam("type", "DURATION").
                get("/paths").
                then().
                log().all().
                extract();
    }

    public static void 최소_시간_경로_응답됨(ExtractableResponse<Response> response, List<Long> expectedPath) {
        PathResponse path = response.as(PathResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(path.getStations().size()).isNotEqualTo(0);
        assertThat(path.getStations()).extracting(StationResponse::getId).containsAnyElementsOf(expectedPath);
    }

    public static void 총_거리와_소요_시간_함께_응답됨(ExtractableResponse<Response> response) {
        PathResponse path = response.as(PathResponse.class);

        assertThat(path.getDistance()).isNotNull();
        assertThat(path.getDuration()).isNotNull();
    }

}
