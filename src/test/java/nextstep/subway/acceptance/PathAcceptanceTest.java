package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
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
	 * When 역과 역 사이의 최단 경로를 요청하면
	 * Then 최단 경로와 거리를 얻는다.
	 */
	public void 최단_경로_조회() {
		// when
		ExtractableResponse<Response> response = 지하철_최단_경로_조회(교대역, 강남역);

		// then
		요청_응답_확인(response, HttpStatus.OK);
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 강남역, 양재역);
		assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
	}

	/**
	 * When 출발역과 도착역이 같은 경우
	 * Then 최단 경로 조회가 실패한다.
	 */
	public void 출발역과_도착역이_같은_경우() {
		// when
		ExtractableResponse<Response> response = 지하철_최단_경로_조회(교대역, 교대역);

		// then
		요청_응답_확인(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * When 연결되어 있지 않은 출발역과 도착역으로 지하철 경로를 조회하면
	 * Then 최단 경로 조회가 실패한다.
	 */
	public void 연결되어_있지_않은_지하철_경로_조회() {
		// when
		Long 사당역 = 지하철역_생성_요청("사당역").jsonPath().getLong("id");
		ExtractableResponse<Response> response = 지하철_최단_경로_조회(교대역, 사당역);

		// then
		요청_응답_확인(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * When 존재하지 않는 역으로 지하철 경로를 조회하면
	 * Then 최단 경로 조회가 실패한다.
	 */
	public void 존재하지_않는_역의_지하철_경로_조회() {
		// when
		Long 존재하지_하지_않는_역 = 4885L;
		ExtractableResponse<Response> response = 지하철_최단_경로_조회(교대역, 존재하지_하지_않는_역);

		// then
		요청_응답_확인(response, HttpStatus.BAD_REQUEST);
	}

	private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId + "");
		params.put("downStationId", downStationId + "");
		params.put("distance", distance + "");
		return params;
	}
}
