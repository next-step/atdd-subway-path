package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class LinePathSteps {
	public static ExtractableResponse<Response> 지하철_최단_경로_조회(Long sourceId, Long targetId) {
		Map<String, String> params = new HashMap<>();
		params.put("source", sourceId + "");
		params.put("target", targetId + "");
		return RestAssured
			.given().log().all()
			.queryParams(params)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/paths")
			.then().log().all().extract();
	}

	public static void 최단_경로_지하철역을_확인(ExtractableResponse<Response> response, Long ...stationIds) {
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(stationIds);
	}

	public static void 최단_경로_거리_확인(ExtractableResponse<Response> response, int distance) {
		assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
	}
}
