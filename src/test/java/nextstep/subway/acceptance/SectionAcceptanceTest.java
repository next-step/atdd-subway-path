package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
	private Long 신분당선;

	private Long 강남역;
	private Long 양재역;

	/**
	 * Given 지하철역과 노선 생성을 요청 하고
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();

		강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
		양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

		Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
		신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
	}

	/**
	 * When 지하철 노선에 새로운 역을 상행 종점역과 하행 종점역 사이에 있는 역으로 하는 새로운 구간 추가를 요청 하면
	 * Then 노선에 새로운 구간이 추가된다
	 */
	@DisplayName("지하철 노선에 구간을 등록 - 새로운 역이 상행 종점역과 하행 종점역 사이에 있는 경우")
	@Test
	void addLineSectionBetweenUpStationAndDownStation() {
		// when
		Long 강재역 = 지하철역_생성_요청("강재역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 강재역, 9));

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsOnly(강남역, 양재역, 강재역);
	}

	/**
	 * When 지하철 노선에 새로운 역을 상행 종점으로 하는 새로운 구간 추가를 요청 하면
	 * Then 노선에 새로운 구간이 추가된다
	 */
	@DisplayName("지하철 노선에 구간을 등록 - 새로운 역이 상행 종점역이 되는 경우")
	@Test
	void addLineSectionToUpStation() {
		// when
		Long 논현역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 강남역, 10));

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsOnly(강남역, 양재역, 논현역);
	}

	/**
	 * When 지하철 노선에 새로운 역을 하행 종점으로 하는 새로운 구간 추가를 요청 하면
	 * Then 노선에 새로운 구간이 추가된다
	 */
	@DisplayName("지하철 노선에 구간을 등록 - 새로운 역이 하행 종점역이 되는 경우")
	@Test
	void addLineSectionToDownStation() {
		// when
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsOnly(강남역, 양재역, 정자역);
	}

	/**
	 * Given 지하철 노선에 새로운 구간 추가를 요청 하고
	 * When 지하철 노선의 마지막 구간 제거를 요청 하면
	 * Then 노선에 구간이 제거된다
	 */
	@DisplayName("지하철 노선에 구간을 제거")
	@Test
	void removeLineSection() {
		// given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

		// when
		지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsOnly(강남역, 양재역);
	}

	private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
		Map<String, String> lineCreateParams;
		lineCreateParams = new HashMap<>();
		lineCreateParams.put("name", "신분당선");
		lineCreateParams.put("color", "bg-red-600");
		lineCreateParams.put("upStationId", upStationId + "");
		lineCreateParams.put("downStationId", downStationId + "");
		lineCreateParams.put("distance", 10 + "");
		return lineCreateParams;
	}

	private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId + "");
		params.put("downStationId", downStationId + "");
		params.put("distance", distance + "");
		return params;
	}
}
