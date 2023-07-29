package nextstep.subway.acceptance;

import static nextstep.subway.utils.LineFixture.경강선_색상;
import static nextstep.subway.utils.LineFixture.경강선_이름;
import static nextstep.subway.utils.LineFixture.수인분당선_색상;
import static nextstep.subway.utils.LineFixture.수인분당선_이름;
import static nextstep.subway.utils.LineFixture.신분당선_색상;
import static nextstep.subway.utils.LineFixture.신분당선_이름;
import static nextstep.subway.utils.LineFixture.지하철_노선_생성;
import static nextstep.subway.utils.LineFixture.지하철_노선의_구간_추가_요청;
import static nextstep.subway.utils.PathFixture.지하철_경로_조회;
import static nextstep.subway.utils.StationFixture.강남역_이름;
import static nextstep.subway.utils.StationFixture.논현역_이름;
import static nextstep.subway.utils.StationFixture.신논현역_이름;
import static nextstep.subway.utils.StationFixture.신사역_이름;
import static nextstep.subway.utils.StationFixture.양재시민의숲역_이름;
import static nextstep.subway.utils.StationFixture.양재역_이름;
import static nextstep.subway.utils.StationFixture.이매역_이름;
import static nextstep.subway.utils.StationFixture.지하철역_생성;
import static nextstep.subway.utils.StationFixture.판교역_이름;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;

public class PathAcceptanceTest extends AcceptanceTest {

	private StationResponse 신사역;
	private StationResponse 논현역;
	private StationResponse 신논현역;
	private StationResponse 판교역;
	private StationResponse 이매역;
	private StationResponse 강남역;
	private StationResponse 양재역;
	private StationResponse 양재시민의숲역;
	private LineResponse 신분당선;
	private LineResponse 경강선;

	/**
	 *  논현역 --- 신분당선 --- 신논현역
	 *    |                     |
	 * 신분당선                경강선
	 *    |                     |
	 *  신사역                 판교역 --- 경강선 --- 이매역
	 *
	 * 강남역 --- 수인분당선 --- 양재역
	 */
	@BeforeEach
	void init() {
		신사역 = 지하철역_생성(신사역_이름);
		논현역 = 지하철역_생성(논현역_이름);
		신논현역 = 지하철역_생성(신논현역_이름);
		판교역 = 지하철역_생성(판교역_이름);
		이매역 = 지하철역_생성(이매역_이름);
		강남역 = 지하철역_생성(강남역_이름);
		양재역 = 지하철역_생성(양재역_이름);
		양재시민의숲역 = 지하철역_생성(양재시민의숲역_이름);

		신분당선 = 지하철_노선_생성(신분당선_이름, 신분당선_색상, 신사역.getId(), 논현역.getId(), 10);
		경강선 = 지하철_노선_생성(경강선_이름, 경강선_색상, 신논현역.getId(), 판교역.getId(), 5);
		지하철_노선_생성(수인분당선_이름, 수인분당선_색상, 강남역.getId(), 양재역.getId(), 5);

		지하철_노선의_구간_추가_요청(신분당선.getId(), 논현역.getId(), 신논현역.getId(), 5);
		지하철_노선의_구간_추가_요청(경강선.getId(), 판교역.getId(), 이매역.getId(), 7);
	}

	/**
	 * Given 지하철 노선과 구간을 생성하고
	 * When 지하철 경로를 조회하면
	 * Then 출발역에서 도착역까지의 경로에 있는 역 목록과 총 거리를 조회할 수 있다.
	 */
	@Test
	void 지하철_경로를_조회한다() {
		// when
		var response = 지하철_경로_조회(논현역.getId(), 판교역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.OK);
		출박역에서_도착역까지의_경로에_있는_역들과_총거리_검증(response, 10, 논현역, 신논현역, 판교역);
	}

	/**
	 * Given 지하철 노선과 구간을 생성하고
	 * When 출발역이 존재하지 않은 역으로 지하철 경로를 조회하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 지하철_경로_조회_시_출발역이_존재하지_않은_역이면_요청이_실패된다() {
		// given
		Long 존재_하지_않은_역_아이디 = 10L;

		// when
		var response = 지하철_경로_조회(존재_하지_않은_역_아이디, 이매역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.NOT_FOUND);
	}

	/**
	 * Given 지하철 노선과 구간을 생성하고
	 * When 도착역이 존재하지 않은 역으로 지하철 경로를 조회하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 지하철_경로_조회_시_도착역이_존재하지_않은_역이면_요청이_실패된다() {
		// given
		Long 존재_하지_않은_역_아이디 = 10L;

		// when
		var response = 지하철_경로_조회(신논현역.getId(), 존재_하지_않은_역_아이디);

		// then
		응답_상태코드_검증(response, HttpStatus.NOT_FOUND);
	}

	/**
	 * Given 지하철 노선과 구간을 생성하고
	 * When 출발역과 도착역이 같을 경우 지하철 경로를 조회하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 지하철_경로_조회_시_출발역과_도착역이_같을_경우_에러를_반환한다() {
		// when
		var response = 지하철_경로_조회(신논현역.getId(), 신논현역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Given 지하철 노선과 구간을 생성하고
	 * When 노선에 등록되지 않은 역으로 지하철 경로를 조회하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 지하철_경로_조회_시_노선에_등록되지_않은_역이면_에러를_반환한다() {
		// when
		var response = 지하철_경로_조회(양재시민의숲역.getId(), 신논현역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Given 지하철 노선과 구간을 생성하고
	 * When 출발역과 도착역이 연결되어 있지 않을 경우 지하철 경로를 조회하면
	 * Then 요청이 실패된다
	 */
	@Test
	void 지하철_경로_조회_시_출발역과_도착역이_연결되어_있지_않으면_에러를_반환한다() {
		// when
		var response = 지하철_경로_조회(신사역.getId(), 양재역.getId());

		// then
		응답_상태코드_검증(response, HttpStatus.BAD_REQUEST);
	}

	private void 응답_상태코드_검증(ExtractableResponse<Response> response, HttpStatus httpStatus) {
		assertThat(response.statusCode()).isEqualTo(httpStatus.value());
	}

	private void 출박역에서_도착역까지의_경로에_있는_역들과_총거리_검증(ExtractableResponse<Response> response, int expectDistance,
		StationResponse... expectStations) {
		int actualDistance = response.jsonPath().getInt("distance");
		List<StationResponse> actualStations = response.jsonPath().getList("stations", StationResponse.class);

		assertThat(actualDistance).isEqualTo(expectDistance);
		assertThat(actualStations).usingRecursiveComparison().isEqualTo(List.of(expectStations));
	}
}
