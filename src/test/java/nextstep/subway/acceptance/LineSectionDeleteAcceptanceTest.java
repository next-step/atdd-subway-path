package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 삭제에 대한 인수 테스트")
class LineSectionDeleteAcceptanceTest extends AcceptanceTest {

	Long 신분당선;
	Long 강남역;
	Long 양재역;
	Long 판교역;
	int 강남_양재_거리 = 7;
	int 양재_판교_거리 = 7;

	/**
	 * Given 신분당선 노선에 역이 순서대로 위치할 경우
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();

		강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
		양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
		판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

		final 노선_생성_파라미터 강남_양재_노선 = new 노선_생성_파라미터(강남역, 양재역, 강남_양재_거리);
		신분당선 = 지하철_노선_생성_요청(강남_양재_노선).jsonPath().getLong("id");

		final 구간_생성_파라미터 양재_판교_구간 = new 구간_생성_파라미터(양재역, 판교역, 양재_판교_거리);
		지하철_노선에_지하철_구간_생성_요청(신분당선, 양재_판교_구간);
	}


	/**
	 * When 노선에 등록되지 않은 구간을 삭제할 경우
	 * Then 예외를 던진다.
	 */
	@DisplayName("지하철 노선에 구간을 제거")
	@Test
	void removeLineSection() {
		// given
		Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

		// when
		final ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * Given 노선에 구간이 하나일 경우
	 * When 구간을 삭제할 경우
	 * Then 예외를 던진다.
	 */
	@DisplayName("노선에 구간이 하나일 때 구간 삭제")
	@Test
	void removeExceptionWhenOneSectionLeft() {
		// given
		지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역);

		// when
		final ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

	}

	/**
	 * When 중간 역을 기준으로 구간 삭제요청하면
	 * Then 중간 역이 삭제되고 구간이 재배치된다.
	 */
	@DisplayName("중간역 기준으로 구간 삭제")
	@Test
	void removeSectionBetweenLastStations() {
		// when
		final ExtractableResponse<Response> 제거_응답 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
		ExtractableResponse<Response> 목록_응답 = 지하철_노선_조회_요청(신분당선);

		// then
		assertThat(제거_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(getStationList(목록_응답)).containsExactly(강남역, 판교역);
	}

	/**
	 * When 상행 종점역을 기준으로 구간 삭제요청하면
	 * Then 마지막 구간이 삭제된다
	 */
	@DisplayName("하행 종점역 기준으로 구간 삭제")
	@Test
	void removeSectionBasedOnLastUpStation() {
		// when
		final ExtractableResponse<Response> 제거_응답 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);
		ExtractableResponse<Response> 목록_응답 = 지하철_노선_조회_요청(신분당선);

		// then
		assertThat(제거_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(getStationList(목록_응답)).containsExactly(양재역, 판교역);
	}

	/**
	 * When 하행 종점역을 기준으로 구간 삭제요청하면
	 * Then 마지막 구간이 삭제된다
	 */
	@DisplayName("하행 종점역 기준으로 구간 삭제")
	@Test
	void removeSectionBasedOnLastDownStation() {
		// when
		final ExtractableResponse<Response> 제거_응답 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역);
		ExtractableResponse<Response> 목록_응답 = 지하철_노선_조회_요청(신분당선);

		// then
		assertThat(제거_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(getStationList(목록_응답)).containsExactly(강남역, 양재역);
	}

}
