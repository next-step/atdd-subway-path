package nextstep.subway.path;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {

	private LineResponse 신분당선;
	private LineResponse 이호선;
	private LineResponse 삼호선;
	private StationResponse 강남역;
	private StationResponse 양재역;
	private StationResponse 교대역;
	private StationResponse 남부터미널역;

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

		강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
		양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
		교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
		남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

		신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역, 10);
		이호선 = 지하철_노선_등록되어_있음("이호선", "bg-red-600", 교대역, 강남역, 10);
		삼호선 = 지하철_노선_등록되어_있음("삼호선", "bg-red-600", 교대역, 양재역, 5);

		지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 3);
	}

	@DisplayName("출발역부터 도착역까지의 최단 경로를 조회한다.")
	@Test
	void findShortestPath() {
		// when
		final ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(강남역, 남부터미널역);

		// then
		지하철_최단경로_응답됨(response);
	}

	@DisplayName("출발역과 도착역이 같은 최단 경로를 조회한다.")
	@Test
	void findShortestPathSameStation() {
		// when
		final ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(강남역, 강남역);

		// then
		지하철_최단경로_조회_실패됨(response);
	}

	@DisplayName("출발역과 도착역이 연결되지 않은 경로를 조회한다.")
	@Test
	void findShortestPathNotConnected() {
		// when
		final ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(강남역, 강남역);

		// then
		지하철_최단경로_조회_실패됨(response);
	}

	@DisplayName("출발역이나 도착역이 존재하지 않는 경로를 조회한다.")
	@Test
	void findShortestPathNotExistStation() {
		// given
		StationResponse 수원역 = 지하철역_등록되어_있음("수원역").as(StationResponse.class);
		StationResponse 천안역 = 지하철역_등록되어_있음("천안역").as(StationResponse.class);
		지하철_노선_등록되어_있음("일호선", "bg-green-600", 수원역, 천안역, 20);

		//when
		final ExtractableResponse<Response> response = 지하철_최단경로_조회_요청(수원역, 강남역);

		// then
		지하철_최단경로_조회_실패됨(response);
	}

	private ExtractableResponse<Response> 지하철_최단경로_조회_요청(
		StationResponse source, StationResponse target) {
		return RestAssured
			.given().log().all()
			.queryParam("source", source.getId())
			.queryParam("target", target.getId())
			.when().get("/paths")
			.then().log().all().extract();
	}

	private void 지하철_최단경로_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getInt("distance")).isNotNull();
	}

	private void 지하철_최단경로_조회_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
