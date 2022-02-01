package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
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
	 * 교대역    --- *2호선*(10) ---   강남역
	 * |                            |
	 * *3호선*(2)                     *신분당선* (10)
	 * |                            |
	 * 남부터미널역  --- *3호선*(3) ---   양재
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();

		교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
		강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
		양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
		남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

		이호선 = 아이디_추출(지하철_노선_생성_요청(new 노선_생성_파라미터("2호선", "green", 교대역, 강남역, 10)));
		신분당선 = 아이디_추출(지하철_노선_생성_요청(new 노선_생성_파라미터("신분당선", "red", 강남역, 양재역, 10)));
		삼호선 = 아이디_추출(지하철_노선_생성_요청(new 노선_생성_파라미터("3호선", "orange", 교대역, 남부터미널역, 2)));

		지하철_노선에_지하철_구간_생성_요청(삼호선, new 구간_생성_파라미터(남부터미널역, 양재역, 3));
	}

	/**
	 * When 두 역 사이의 최단 경로 검색을 요청하면
	 * Then 최단 경로 위의 역들을 반환 받는다.
	 * Then 두 역 사이의 최단 거리를 반환 받는다.
	 */
	@DisplayName("최단 경로 검색")
	@Test
	void findShortestPath() {
		// when
		ExtractableResponse<Response> 경로_검색_응답 = 지하철역_경로_검색_요청(교대역, 양재역);

		List<Long> 최단_경로_역_목록 = 역_아이디_목록_추출(경로_검색_응답);
		int 최단_경로_거리 = 최단_경로_거리_추출(경로_검색_응답);

		// then
		assertThat(최단_경로_역_목록).containsExactly(교대역, 남부터미널역, 양재역);
		assertThat(최단_경로_거리).isEqualTo(5);
	}

}
