package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철_노선_최단거리_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.ui.error.exception.ErrorCode.SAME_SOURCE_AND_TARGET;
import static nextstep.subway.ui.error.exception.ErrorCode.SECTION_NOT_LONGER_THEN_EXISTING_SECTION;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {
	private Long 교대역;
	private Long 강남역;
	private Long 양재역;
	private Long 남부터미널역;
	private Long 이호선;
	private Long 신분당선;
	private Long 삼호선;

	/**
	 * 교대역    --- *2호선* ---   강남역
	 * |                        |
	 * *3호선*                   *신분당선*
	 * |                        |
	 * 남부터미널역  --- *3호선* ---   양재
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();

		교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
		강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
		양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
		남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

		이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
		신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
		삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

		지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
	}


	/**
	 * Given 출발역과 도착역을 제공하고
	 * When 최단경로 조회를 요청 하면
	 * Then 최단경로에 있는 역 목록과 경로 구간의 거리가 조회된다.
	 */
	@DisplayName("최단경로 조회")
	@Test
	void getPath(){
		//given
		Long 출발역 = 교대역;
		Long 도착역 = 양재역;
		//when
		ExtractableResponse<Response> response = 지하철_노선_최단거리_조회(출발역, 도착역);
		//then
		assertThat(response.jsonPath().getList("stations.id")).containsExactly(교대역.intValue(), 남부터미널역.intValue(), 양재역.intValue());
		assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
	}

	/**
	 * Given 같은 역으로 출발역과 도착역을 제공하고
	 * When 최단경로 조회를 요청 하면
	 * Then 최단경로에 있는 역 목록과 경로 구간의 거리가 조회가 되지 않는다.
	 */
	@DisplayName("최단경로 조회- 출발역과 도착역이 같은 경우 예외처리 검증")
	@Test
	void getPathWithSameStations(){
		//given
		Long 출발역 = 교대역;
		Long 도착역 = 교대역;
		//when
		ExtractableResponse<Response> response = 지하철_노선_최단거리_조회(출발역, 도착역);
		//then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.jsonPath().getList("errorMessages")).contains(SAME_SOURCE_AND_TARGET.getMessage());
	}

	//TODO: 출발역과 도착역이 연결이 되어 있지 않은 경우 예외처리 검증
	//TODO: 존재하지 않은 출발역이나 도착역을 조회 할 경우 예외처리 검증

	private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId + "");
		params.put("downStationId", downStationId + "");
		params.put("distance", distance+"");
		return params;
	}
}