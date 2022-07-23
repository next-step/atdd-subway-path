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
class LineSectionAcceptanceTest extends AcceptanceTest {
	private Long 신분당선;
	private Long 강남역;
	private Long 양재역;
	private Long 양재시민의숲역;
	private Long 신논현역;
	private long DISTANCE_VALUE_9 = 9;
	private long DISTANCE_VALUE_5 = 5;

	/**
	 * Given 지하철역과 노선 생성을 요청 하고
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();

		강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
		양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
		양재시민의숲역 = 지하철역_생성_요청("양재시민의숲").jsonPath().getLong("id");
		신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");

		Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
		신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
	}

	/**
	 * When 지하철 노선에 새로운 구간 추가를 요청 하면
	 * Then 노선에 새로운 구간이 추가된다
	 */
	@DisplayName("지하철 노선에 구간을 등록")
	@Test
	void addLineSection() {
		// when
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_VALUE_9));

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
	}

	/**
	 * When 지하철 노선에 기존 구간과 동일한 구간 추가를 요청 하면
	 * Then 오류
	 */
	@DisplayName("지하철 노선에 구간을 등록")
	@Test
	void addLineSameSection() {
		// when
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_VALUE_9));

		// then
		ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
			createSectionCreateParams(양재역, 정자역, DISTANCE_VALUE_9));
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * When 지하철 노선에 기존 구간과 동일한 역이 없는 구간 추가를 요청 하면
	 * Then 오류
	 */
	@DisplayName("지하철 노선에 구간을 등록")
	@Test
	void addLineSectionWithoutSameStations() {
		// when
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_VALUE_9));

		// then
		ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
			createSectionCreateParams(신논현역, 양재시민의숲역, DISTANCE_VALUE_9));
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 *  Given 지하철 노선에 새로운 구간 추가 요청하고 ( A역  - C역 )
	 *  When 추가된 지하철 구간 상행역과 하행역사이에 새로운 구간을 요청하면 ( A역  - B역 )
	 *  Then 새로운 구간이 추가된다. ( A역  - B역 - C역 )
	 */
	@DisplayName("기존 구간사이에 구간 추가")
	@Test
	void addLineTopSectionBaseOnUpStation() {
		//Given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_VALUE_9));
		// 강남 - 양재 - 정자
		//When
		Long 양재시민의숲 = 지하철역_생성_요청("양재시민의숲").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲, DISTANCE_VALUE_5));

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 양재시민의숲, 정자역);
	}

	/**
	 *  Given 지하철 노선에 새로운 구간 추가 요청하고 ( A역  - C역 )
	 *  When 추가된 지하철 구간 상행역과 위에 새로운 구간을 요청하면 ( B역  - A역 )
	 *  Then 새로운 구간이 추가된다. ( B역  - A역 - C역 )
	 */
	@DisplayName("상행 종점에 구간 추가")
	@Test
	void addLineDownSectionBaseOnUpStation() {
		//Given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_VALUE_9));

		//When
		Long 신논현역 = 지하철역_생성_요청("신논현").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, DISTANCE_VALUE_9));

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신논현역, 강남역, 양재역, 정자역);
	}

	/**
	 *  Given 지하철 노선에 새로운 구간 추가 요청하고 ( A역  - C역 )
	 *  When 추가된 지하철 구간 상행역과 위에 새로운 구간을 요청하면 ( C역  - D역 )
	 *  Then 새로운 구간이 추가된다. ( A역  - C역 - D역 )
	 */
	@DisplayName("하행 종점에 신규 구간 추가")
	@Test
	void AddSectionToEndOfDownStation() {
		//Given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_VALUE_9));

		//When
		Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역, DISTANCE_VALUE_9));

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역, 미금역);
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
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_VALUE_9));

		// when
		지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

		// then
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
	}

	/**
	 * Given 지하철 노선에 새로운 구간 추가를 요청 하고
	 * When 지하철 노선의 마지막 구간이 아닌역 제거를 요청 하면
	 * Then 400 오류 응답을 받는다
	 */
	@DisplayName("지하철 노선에 구간을 제거")
	@Test
	void removeLineNotLastSection() {
		// given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

		// when
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_VALUE_9));
		지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲역, DISTANCE_VALUE_5));

		// then
		ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재시민의숲역);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

	private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, long distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId + "");
		params.put("downStationId", downStationId + "");
		params.put("distance", distance + "");
		return params;
	}
}
