package nextstep.subway.acceptance;

import static nextstep.subway.utils.LineFixture.*;
import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;

@DisplayName("지하철 구간 관리 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	private StationResponse 신사역;
	private StationResponse 논현역;
	private StationResponse 신논현역;
	private StationResponse 강남역;
	private StationResponse 양재역;
	private LineResponse 신분당선;

	@BeforeEach
	void init() {
		신사역 = 지하철역_생성(신사역_이름);
		논현역 = 지하철역_생성(논현역_이름);
		신논현역 = 지하철역_생성(신논현역_이름);
		강남역 = 지하철역_생성(강남역_이름);
		양재역 = 지하철역_생성(양재역_이름);
		신분당선 = 지하철_노선_리스폰_변환(지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 신사역.getId(), 논현역.getId(), 10));
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선에 구간을 추가하면
	 * Then 지하철 노선 조회 시 생성된 구간을 찾을 수 있다
	 */
	@Test
	void 지하철_노선에_구간을_추가한다() {
		// when
		var response = 지하철_노선의_구간_추가_요청(신분당선.getId(), 신논현역.getId(), 논현역.getId(), 5);

		// then
		응답_상태코드_검증(response, HttpStatus.CREATED);
		지하철_노선의_역들을_조회하여_변경된_구간을_검증(신분당선.getId(), 신사역, 신논현역, 논현역);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 새로운 역을 기존 역 사이로 구간을 추가하면
	 * Then 지하철 노선 목록 조회 시 생성 및 수정된 구간을 찾을 수 있다
	 */
	@Test
	void 지하철_노선에_새로운_역을_기존_역_사이로_구간을_추가한다() {
		// when
		var response = 지하철_노선의_구간_추가_요청(신분당선.getId(), 신사역.getId(), 신논현역.getId(), 7);

		// then
		응답_상태코드_검증(response, HttpStatus.CREATED);
		지하철_노선의_역들을_조회하여_변경된_구간을_검증(신분당선.getId(), 신사역, 신논현역, 논현역);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 새로운 역을 상행 종점으로 구간을 추가하면
	 * Then 지하철 노선 목록 조회 시 생성 및 수정된 구간을 찾을 수 있다
	 */
	@Test
	void 지하철_노선에_새로운_역을_상행_종점으로_구간을_추가한다() {
		// when
		var response = 지하철_노선의_구간_추가_요청(신분당선.getId(), 신논현역.getId(), 신사역.getId(), 10);

		// then
		응답_상태코드_검증(response, HttpStatus.CREATED);
		지하철_노선의_역들을_조회하여_변경된_구간을_검증(신분당선.getId(), 신논현역, 신사역, 논현역);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 새로운 역을 하행 종점으로 구간을 추가하면
	 * Then 지하철 노선 목록 조회 시 생성 및 수정된 구간을 찾을 수 있다
	 */
	@Test
	void 지하철_노선에_새로운_역을_하행_종점으로_구간을_추가한다() {
		// when
		var response = 지하철_노선의_구간_추가_요청(신분당선.getId(), 논현역.getId(), 신논현역.getId(), 10);

		// then
		응답_상태코드_검증(response, HttpStatus.CREATED);
		지하철_노선의_역들을_조회하여_변경된_구간을_검증(신분당선.getId(), 신사역, 논현역, 신논현역);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 새로운 역을 기존 역 사이 길이보다 길거나 같게 구간을 추가하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 구간을_추가할_때_새로운_역을_역_사이에_추가할_경우_기존_역_사이_길이보다_길거나_같으면_요청이_실패된다() {
		// when
		var response = 지하철_노선의_구간_추가_요청(신분당선.getId(), 신사역.getId(), 신논현역.getId(), 10);

		// then
		응답_상태코드_검증(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 상행역과 하행역이 이미 노선에 모두 등록된 상태로 구간을 추가하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 구간을_추가할_때_상행역과_하행역이_이미_노선에_모두_등록되어_있다면_요청이_실패된다() {
		// when
		var response = 지하철_노선의_구간_추가_요청(신분당선.getId(), 신사역.getId(), 논현역.getId(), 5);

		// then
		응답_상태코드_검증(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 상행역과 하행역이 둘 다 포함되어 있지 않은 상태로 구간을 추가하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 구간을_추가할_때_상행역과_하행역이_둘_다_포함되어_있지_않다면_요청이_실패된다() {
		/// when
		var response = 지하철_노선의_구간_추가_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 10);

		// then
		응답_상태코드_검증(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Given 자하철 노선에 구간을 포함하여 생성하고
	 * When 지하철 노선에 중간 구간 삭제하면
	 * Then 지하철 노선 조회 시 삭제한 구간을 찾을 수 없다
	 */
	@Test
	void 지하철_노선에_중간_구간을_삭제한다() {
		// given
		지하철_노선의_구간_추가_요청(신분당선.getId(), 논현역.getId(), 신논현역.getId(), 10);

		// when
		var response = 지하철_노선의_구간_삭제_요청(신분당선.getId(), 논현역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.NO_CONTENT);
		지하철_노선의_역들을_조회하여_변경된_구간을_검증(신분당선.getId(), 신사역, 신논현역);
	}

	/**
	 * Given 자하철 노선에 구간을 포함하여 생성하고
	 * When 지하철 노선에 상행 종점역인 구간 삭제하면
	 * Then 지하철 노선 조회 시 삭제한 구간을 찾을 수 없다
	 */
	@Test
	void 지하철_노선에_상행_종점역인_구간을_삭제한다() {
		// given
		지하철_노선의_구간_추가_요청(신분당선.getId(), 논현역.getId(), 신논현역.getId(), 10);

		// when
		var response = 지하철_노선의_구간_삭제_요청(신분당선.getId(), 신사역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.NO_CONTENT);
		지하철_노선의_역들을_조회하여_변경된_구간을_검증(신분당선.getId(), 논현역, 신논현역);
	}

	/**
	 * Given 자하철 노선에 구간을 포함하여 생성하고
	 * When 지하철 노선에 하행 종점역인 구간 삭제하면
	 * Then 지하철 노선 조회 시 삭제한 구간을 찾을 수 없다
	 */
	@Test
	void 지하철_노선에_하행_종점역인_구간을_삭제한다() {
		// given
		지하철_노선의_구간_추가_요청(신분당선.getId(), 논현역.getId(), 신논현역.getId(), 10);

		// when
		var response = 지하철_노선의_구간_삭제_요청(신분당선.getId(), 신논현역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.NO_CONTENT);
		지하철_노선의_역들을_조회하여_변경된_구간을_검증(신분당선.getId(), 신사역, 논현역);
	}

	/**
	 * Given 자하철 노선에 구간을 포함하여 생성하고
	 * When 지하철 노선에 마지막 구간을 삭제하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 구간을_삭제할_때_마지막_구간이면_요청이_실패된다() {
		// when
		var response = 지하철_노선의_구간_삭제_요청(신분당선.getId(), 신논현역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Given 자하철 노선에 구간을 포함하여 생성하고
	 * When 지하철 노선에 등록 되어있지 않은 역의 구간을 삭제하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 구간을_삭제할_때_노선에_등록_되어있지_않은_역이면_요청이_실패된다() {
		// given
		지하철_노선의_구간_추가_요청(신분당선.getId(), 논현역.getId(), 신논현역.getId(), 10);

		// when
		var response = 지하철_노선의_구간_삭제_요청(신분당선.getId(), 강남역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.BAD_REQUEST);
	}

	private void 응답_상태코드_검증(ExtractableResponse<Response> response, HttpStatus httpStatus) {
		assertThat(response.statusCode()).isEqualTo(httpStatus.value());
	}

	private void 지하철_노선의_역들을_조회하여_변경된_구간을_검증(Long lineId, StationResponse... expectStationResponses) {
		List<StationResponse> actual = 지하철_노선_조회_요청(lineId).jsonPath()
			.getList("stations", StationResponse.class);

		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(List.of(expectStationResponses));
	}
}
