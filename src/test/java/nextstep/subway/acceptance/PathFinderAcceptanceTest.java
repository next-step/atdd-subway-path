package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderAcceptanceTest extends AcceptanceTest {
	private Long 이호선;
	private Long 구호선;
	private Long 공항철도선;

	private Long 홍대입구역;
	private Long 합정역;
	private Long 당산역;
	private Long 김포공항역;

	/**
	 * 합정역    --- *2호선* ---   홍대입구역
	 * |             10            |
	 * *2호선* 20              *공항철도선* 20
	 * |            20            |
	 * 당산역  --- *9호선* ---   김포공항역
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();

		홍대입구역 = StationSteps.지하철역_생성_요청("홍대입구역").jsonPath().getLong("id");
		합정역 = StationSteps.지하철역_생성_요청("합정역").jsonPath().getLong("id");
		당산역 = StationSteps.지하철역_생성_요청("당산역").jsonPath().getLong("id");
		김포공항역 = StationSteps.지하철역_생성_요청("김포공항역").jsonPath().getLong("id");

		이호선 = LineSteps.지하철_노선_생성_요청("2호선", "green", 홍대입구역, 합정역, 10).jsonPath().getLong("id");
		LineSteps.지하철_노선에_지하철_구간_생성_요청(이호선, 합정역, 당산역, 20);

		구호선 = LineSteps.지하철_노선_생성_요청("9호선", "gold", 당산역, 김포공항역, 20).jsonPath().getLong("id");
		공항철도선 = LineSteps.지하철_노선_생성_요청("공항철도선", "blue", 홍대입구역, 김포공항역, 20).jsonPath().getLong("id");
	}

	/**
	 * When 출발역과 도착역으로 최단 경로 조회 요청하면
	 * Then 최단 경로가 조회 성공한다.
	 */
	@DisplayName("출발역과 도착역으로 최단 경로를 조회 성공한다.")
	@Test
	void findPath() {
		ExtractableResponse<Response> response = PathFinderSteps.최단경로_조회(합정역, 김포공항역);

		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(합정역, 홍대입구역, 김포공항역);
	}

	/**
	 * When 출발역과 도착역을 동일한역으로 최단 경로 조회 요청하면
	 * Then 최단 경로가 조회 실패한다.
	 */
	@DisplayName("출발역과 도착역이 같을 경우 최단 경로를 조회 실패한다.")
	@Test
	void findSameStationRoute() {
		ExtractableResponse<Response> response = PathFinderSteps.최단경로_조회(합정역, 합정역);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
