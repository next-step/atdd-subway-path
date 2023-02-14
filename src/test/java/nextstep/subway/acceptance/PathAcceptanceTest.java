package nextstep.subway.acceptance;

import static nextstep.subway.steps.LineSteps.*;
import static nextstep.subway.steps.PathSteps.*;
import static nextstep.subway.steps.SectionSteps.*;
import static nextstep.subway.steps.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

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
	 * 교대역 -------- *2호선* ---- 강남역
	 * |                          |
	 * *3호선*                     *신분당선*
	 * |                          |
	 * 남부터미널역 --- *3호선* --- 양재역
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();

		교대역 = createStation("교대역").jsonPath().getLong("id");
		강남역 = createStation("강남역").jsonPath().getLong("id");
		양재역 = createStation("양재역").jsonPath().getLong("id");
		남부터미널역 = createStation("남부터미널역").jsonPath().getLong("id");

		이호선 = createLine("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
		신분당선 = createLine("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
		삼호선 = createLine("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

		createSection(삼호선, 남부터미널역, 양재역, 3);
	}

	/**
	 * When 출발역과 목적지역의 경로를 조회하면
	 * Then 경로와 길이가 짧은 5가 조회된다.
	 * */
	@DisplayName("길이가 짧은 경로를 찾는다")
	@Test
	public void pathSearch() {
		// when
		ExtractableResponse<Response> searchResponse = searchPath(교대역, 양재역);
		assertThat(searchResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

		// then
		assertThat(searchResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
		assertThat(searchResponse.jsonPath().getInt("distance")).isEqualTo(5);
	}

	/**
	 * When 출발역과 도착역이 같은 경로를 조회하면
	 * Then 400에러 발생
	 * */
	@DisplayName("출발역과 도착역이 같은 경로를 조회")
	@Test
	public void noDifferentStation() {
		// when
		ExtractableResponse<Response> searchResponse = searchPath(교대역, 교대역);

		// then
		assertThat(searchResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * Given 새로운 역을 등록한 뒤
	 * When 출발역과 도착역이 연결이 되지 않은 경로를 조회하면
	 * Then 400에러 발생
	 * */
	@DisplayName("연결되지 않는 역을 조회")
	@Test
	public void noConnectSection() {
		// given
		Long 사당역 = createStation("사당역").jsonPath().getLong("id");

		// when
		ExtractableResponse<Response> searchResponse = searchPath(교대역, 사당역);

		// then
		assertThat(searchResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * When 출발역이나 도착역이 존재하지 않는 경로를 조회하면
	 * Then 400에러 발생
	 * */
	@DisplayName("존재하지 않는 역을 조회")
	@Test
	public void noExistStation() {
		// when
		ExtractableResponse<Response> searchResponse = searchPath(교대역, 9999L);

		// then
		assertThat(searchResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
