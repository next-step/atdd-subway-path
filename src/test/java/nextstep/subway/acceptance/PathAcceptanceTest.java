package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("경로 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {
	private Long 신분당선;
	private Long 이호선;
	private Long 삼호선;
	private Long 구호선;
	private Long 신사역;
	private Long 논현역;
	private Long 신논현역;
	private Long 강남역;
	private Long 양재역;
	private Long 고속터미널역;
	private Long 교대역;
	private Long 남부터미널역;

	/* Line
	  신사역 --- *신분당선* ---    논현역
	  |                        |
	  *3호선*                   *신분당선*
	  |                        |
	  고속터미널역  --- *9호선* --- 신논현역
	  |                        |
	  *3호선*                   *신분당선*
	  |                        |
	  교대역   --- *2호선* ---    강남역
	  |                        |
	  *3호선*                   *신분당선*
	  |                        |
	  남부터미널역  --- *3호선* --- 앙재역
	 */

	/** Distance
	 * 신사역 ---   10   ---    논현역
	 * |                        |
	 * 3                        5
	 * |                        |
	 * 고속터미널역  --- 9 ---    신논현역
	 * |                        |
	 * 5                        6
	 * |                        |
	 * 교대역   ---  2   ---    강남역
	 * |                        |
	 * 1                        7
	 * |                        |
	 * 남부터미널역  --- 3 ---    앙재역
	 */

	@BeforeEach
	public void setUp() {
		// Given
		강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
		신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
		양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
		논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
		고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");
		남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
		신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
		교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");

		신분당선 = 지하철_노선_생성_요청("신분당선", "red", 신사역, 논현역, 10).jsonPath().getLong("id");
		이호선 = 지하철_노선_생성_요청("이호선", "green", 교대역, 강남역, 2).jsonPath().getLong("id");
		삼호선 = 지하철_노선_생성_요청("삼호선", "orange", 신사역, 고속터미널역, 3).jsonPath().getLong("id");
		구호선 = 지하철_노선_생성_요청("구호선", "yellow", 고속터미널역, 신논현역, 9).jsonPath().getLong("id");

		지하철_노선에_지하철_구간_생성_요청(신분당선, 논현역, 신논현역, 5);
		지하철_노선에_지하철_구간_생성_요청(신분당선, 신논현역, 강남역, 6);
		지하철_노선에_지하철_구간_생성_요청(신분당선, 강남역, 양재역, 7);
		지하철_노선에_지하철_구간_생성_요청(삼호선, 고속터미널역, 교대역, 5);
		지하철_노선에_지하철_구간_생성_요청(삼호선, 교대역, 남부터미널역, 1);
		지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
	}

	/**
	 * Given 출발역과 도착역이 주어지고
	 * When 출발역에서 도착역 까지의 경로를 요청하면
	 * Then 출발역과 도착역까지의 경로를 조회할 수 있다.
	 */
	@DisplayName("지하철 경로 조회 - 거치는 정점의 갯수가 같은 경우 짧은 거리를 선택")
	@Test
	void getPath_WHEN_SAME_VERTEX_RETURN_SHORTEST_DISTANCE() {
		// Given
		Long 출발역 = 신사역;
		Long 도착역 = 신논현역;

		// When
		ExtractableResponse<Response> response = 경로_조회_요청(출발역, 도착역);

		// Then
		assertAll(
			() -> 응답_상태_검증(response, HttpStatus.OK),
			() -> 경로_역_순서_검증(response, List.of(신사역, 고속터미널역, 신논현역)),
			() -> 경로_거리_검증(response, 12)
		);
	}

	/**
	 * Given 출발역과 도착역이 주어지고
	 * When 출발역에서 도착역 까지의 경로를 요청하면
	 * Then 출발역과 도착역까지의 경로를 조회할 수 있다.
	 */
	@DisplayName("지하철 경로 조회 - 거치는 정점의 갯수가 많아도 짧은 거리를 선택")
	@Test
	void getPath_WHEN_MORE_VERTEX_RETURN_SHORTEST_DISTANCE() {
		// Given
		Long 출발역 = 강남역;
		Long 도착역 = 양재역;

		// When
		ExtractableResponse<Response> response = 경로_조회_요청(출발역, 도착역);

		// Then
		assertAll(
			() -> 응답_상태_검증(response, HttpStatus.OK),
			() -> 경로_역_순서_검증(response, List.of(강남역, 교대역, 남부터미널역, 양재역)),
			() -> 경로_거리_검증(response, 6)
		);
	}

	/**
	 * Given 출발역과 도착역이 주어지고
	 * When 출발역에서 도착역 까지의 경로를 요청하면
	 * Then 출발역과 도착역까지의 경로를 조회할 수 있다.
	 */
	@DisplayName("지하철 경로 조회 - 거치는 정점의 갯수가 적은 경우 짧은 거리를 선택")
	@Test
	void getPath_WHEN_LESS_VERTEX_RETURN_SHORTEST_DISTANCE() {
		// Given
		Long 출발역 = 교대역;
		Long 도착역 = 남부터미널역;

		// When
		ExtractableResponse<Response> response = 경로_조회_요청(출발역, 도착역);

		// Then
		assertAll(
			() -> 응답_상태_검증(response, HttpStatus.OK),
			() -> 경로_역_순서_검증(response, List.of(교대역, 남부터미널역)),
			() -> 경로_거리_검증(response, 1)
		);
	}

	/**
	 * Given 출발역과 도착역이 주어지고
	 * When 출발역에서 도착역 까지의 경로를 요청하면
	 * Then 출발역과 도착역까지의 경로를 조회할 수 있다.
	 */
	@DisplayName("지하철 경로 조회 - 환승이 많더라도 짧은 거리를 선택")
	@Test
	void getPath_WHEN_MORE_TRANSFER_RETURN_SHORTEST_DISTANCE() {
		// Given
		Long 출발역 = 남부터미널역;
		Long 도착역 = 신논현역;

		// When
		ExtractableResponse<Response> response = 경로_조회_요청(출발역, 도착역);

		// Then
		assertAll(
			() -> 응답_상태_검증(response, HttpStatus.OK),
			() -> 경로_역_순서_검증(response, List.of(남부터미널역, 교대역, 강남역, 신논현역)),
			() -> 경로_거리_검증(response, 9)
		);
	}

	private void 경로_거리_검증(ExtractableResponse<Response> response, int distance) {
		assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
	}

	private void 경로_역_순서_검증(ExtractableResponse<Response> response, List<Long> stations) {
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(
			stations.toArray(new Long[stations.size()]));
	}

	private void 응답_상태_검증(ExtractableResponse<Response> response, HttpStatus status) {
		assertThat(response.statusCode()).isEqualTo(status.value());
	}
}
