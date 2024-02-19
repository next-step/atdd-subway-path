package subway.acceptance.path;

import static org.assertj.core.api.Assertions.*;
import static subway.fixture.acceptance.LineAcceptanceSteps.*;
import static subway.fixture.acceptance.StationAcceptanceSteps.*;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.acceptance.AcceptanceTest;
import subway.dto.line.LineResponse;
import subway.dto.path.PathResponse;
import subway.station.Station;
import subway.utils.enums.Location;
import subway.utils.rest.Rest;

class PathAcceptanceTest extends AcceptanceTest {
	private Long 교대역_ID;
	private Long 양재역_ID;
	private Long 강남역_ID;
	private Long 남부터미널역_ID;

	private ExtractableResponse<Response> 교대역;
	private ExtractableResponse<Response> 남부터미널역;
	private ExtractableResponse<Response> 양재역;
	private ExtractableResponse<Response> 강남역;

	@BeforeEach
	void setUp() {
		교대역 = 정류장_생성("교대역");
		남부터미널역 = 정류장_생성("남부터미널역");
		양재역 = 정류장_생성("양재역");
		강남역 = 정류장_생성("강남역");

		교대역_ID = 교대역.jsonPath().getLong("id");
		양재역_ID = 양재역.jsonPath().getLong("id");
		강남역_ID = 강남역.jsonPath().getLong("id");
		남부터미널역_ID = 남부터미널역.jsonPath().getLong("id");

		노선_생성("2호선", "green", 교대역_ID, 강남역_ID, 10);
		노선_생성("신분당선", "red", 강남역_ID, 양재역_ID, 10);
		LineResponse 삼호선 = 노선_생성("3호선", "orange", 교대역_ID, 남부터미널역_ID, 2);
		노선_구간_추가(삼호선.getId(), 남부터미널역_ID, 양재역_ID, 3);
	}

	/**
	 * Given 지하철 경로 노선 탐색을 위한, 노선을 생성한다.
	 *       교대역    --- *2호선* ---   강남역
	 *       |                        |
	 *       *3호선*                   *신분당선*
	 *       |                        |
	 *       남부터미널역  --- *3호선* ---   양재
	 * When 출발 지점(source)과 목표 지점(target)을 입력 받아, 최단 거리로 갈수 있는 경로를 탐색한다.
	 * Then 지하철의 경로와 최단 거리를 담은 값을 응답한다.
	 */
	@DisplayName("지하철 노선의 최단 거리를 검색한다.")
	@Test
	void successPaths() {
		// then
		assertThat(최단_경로_조회_요청()).usingRecursiveComparison().isEqualTo(최단_경로());
	}

	private PathResponse 최단_경로_조회_요청() {
		return Rest.builder()
			.uri(Location.PATHS.path())
			.get(params())
			.as(PathResponse.class);
	}

	private HashMap<String, String> params() {
		HashMap<String, String> params = new HashMap<>();
		params.put("source", String.valueOf(교대역_ID));
		params.put("target", String.valueOf(양재역_ID));
		return params;
	}

	private PathResponse 최단_경로() {
		List<Station> stations = List.of(교대역.as(Station.class), 남부터미널역.as(Station.class), 양재역.as(Station.class));
		return new PathResponse(stations, 5);
	}
}
