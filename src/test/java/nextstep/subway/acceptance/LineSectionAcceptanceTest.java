package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
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
	 * When 하행종점역을 상행역으로 하는 구간을 등록 요청하면
	 * Then 노선에 새로운 구간이 추가된다
	 */
	@DisplayName("하행종점역을 상행역으로하는 구간을 등록요청할경우 하행종점역으로 등록된다")
	@Test
	void 하행종점역을_상행역으로하는_구간을_등록요청할경우_하행종점역으로_등록된다() {
		// when
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(강남역, 양재역, 정자역)
		);
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
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

		// when
		지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
	}

	/**
	 * Given 지하철역을 생성 요청을 하고
	 * Given 지하철 노선에 새로운 구간 추가를 요청 하고
	 * Given 지하철역을 생성 요청을 하고
	 * When A-C에 신규 구간 A-B를 추가하면
	 * Then 노선의 A-B B-C 구간이 생성된다
	 */
	@DisplayName("기존구간사이에 새로운 구간추가시 2개의 구간으로 생성된다")
	@Test
	void 기존구간사이에_새로운_구간추가시_2개의_구간으로_생성된다() {
		// given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));
		Long 판교역 = 지하철역_생성_요청("판교").jsonPath().getLong("id");

		// when
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 판교역, 3));

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(강남역, 양재역, 판교역, 정자역)
		);
	}

	/**
	 * Given 지하철역을 생성 요청을 하고
	 * Given 지하철 노선에 새로운 2개의 구간 추가를 요청 하고
	 * When 지하철역 노선 조회를 하면
	 * Then 구간 순서에 맞게 역들이 정렬되어 조회된다
	 */
	@DisplayName("지하철노선 조회시 구간순서에 맞게 역이 정렬되어 조회된다")
	@Test
	void 지하철노선_조회시_구간순서에_맞게_역이_정렬되어_조회된다() {
		// given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));
		Long 판교역 = 지하철역_생성_요청("판교").jsonPath().getLong("id");

		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 판교역, 3));

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

		// then
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(강남역, 양재역, 판교역, 정자역)
		);
	}

	/**
	 * Given 지하철역을 생성 요청을 하고
	 * Given 지하철 노선에 새로운 구간 추가를 요청 하고
	 * When 하행 종점역 제거를 요청하면
	 * Then 종점역이 다음에 오는 역으로 변경된다
	 */
	@DisplayName("하행 종점역 제거 요청시 다음에오는역이 종점역이 된다")
	@Test
	void 하행_종점역_제거_요청시_다음에오는역이_종점역이_된다() {
		// given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

		// when
		지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(강남역, 양재역)
		);
	}

	/**
	 * Given 지하철역을 생성 요청을 하고
	 * Given 지하철 노선에 새로운 구간 추가를 요청 하고
	 * When 상행 종점역 제거를 요청하면
	 * Then 종점역이 다음에 오는 역으로 변경된다
	 */
	@DisplayName("상행 종점역 제거 요청시 다음에오는역이 종점역이 된다")
	@Test
	void 상행_종점역_제거_요청시_다음에오는역이_종점역이_된다() {
		// given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

		// when
		지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(양재역, 정자역)
		);
	}

	/**
	 * Given 지하철역을 생성 요청을 하고
	 * Given 지하철 노선에 새로운 구간 추가를 요청 하고
	 * When A - B - C 역중 중간 B 역 제거를 요청하면
	 * Then A - C 구간으로 구간이 재배치 된다
	 */
	@DisplayName("중간역 제거요청시 기존 2개의 구간이 하나의 구간으로 재배치된다")
	@Test
	void 중간역_제거요청시_기존_2개의_구간이_하나의_구간으로_재배치된다() {
		// given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

		// when
		지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class))
				.containsExactly(강남역, 정자역)
		);
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

	private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId + "");
		params.put("downStationId", downStationId + "");
		params.put("distance", 6 + "");
		return params;
	}

	private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId + "");
		params.put("downStationId", downStationId + "");
		params.put("distance", distance + "");
		return params;
	}
}
