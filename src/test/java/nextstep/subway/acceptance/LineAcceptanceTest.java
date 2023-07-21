package nextstep.subway.acceptance;

import static nextstep.subway.utils.LineFixture.*;
import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.LineDuplicationNameException;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import nextstep.subway.utils.LineFixture;
import nextstep.subway.utils.StationFixture;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	/**
	 * When 지하철 노선을 생성하면
	 * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
	 */
	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given

		// when
		var response = 지하철_노선_생성_요청();

		// then
		지하철_노선_생성됨(response);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 중복된 노선이름으로 지하철 노선을 생성하면
	 * Then 요청이 실패된다
	 */
	@DisplayName("지하철 노선을 생성할 때 중복된 이름이 있으면 실패한다.")
	@Test
	void crateLineFail() {
		// given
		지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선_중복된_이름으로_생성_요청();

		// then
		중복된_이름으로_노선_생성이_실패됨(response);
	}

	/**
	 * When 존재하지 않는 지하철역으로 지하철 노선을 생성하면
	 * Then 요청이 실패된다
	 */
	@DisplayName("지하철 노선을 생성할 때 해당 지하철역이 없으면 실패한다.")
	@Test
	void crateLineFail2() {
		// given

		// when
		var response = 존재하지_않는_지하철역으로_지하철_노선_생성_요청();

		// then
		지하철역이_존재_하지_않아_노선_생성이_실패됨(response);
	}

	/**
	 * Given 2개의 지하철 노선을 생성하고
	 * When 지하철 노선 목록을 조회하면
	 * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
	 */
	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void showLines() {
		// given
		var 신분당선 = 지하철_신분당선_노선_생성();
		var 경강선 = 지하철_경강선_노선_생성();

		// when
		ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

		// then
		지하철_노선_목록_조회됨(response, 신분당선, 경강선);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 조회하면
	 * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
	 */
	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void showLine() {
		// given
		var 신분당선 = 지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선_조회_요청(신분당선.getId());

		// then
		지하철_노선_조회됨(response, 신분당선);
	}

	/**
	 * When 지하철 노신이 없는 노선을 조회하면
	 * Then 요청이 실패된다
	 */
	@DisplayName("지하철 노선을 조회할 때 해당 지하철 노선이 없으면 실패한다.")
	@Test
	void showLineFail() {
		// given

		// when
		var response = 지하철_노선_조회_요청(1L);

		// then
		지하철_노선이_존재_하지_않아_실패됨(response);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 수정하면
	 * Then 해당 지하철 노선 정보는 수정된다
	 */
	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		var 신분당선 = 지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선_수정_요청(신분당선.getId(), 경강선_이름, 경강선_색상);

		// then
		지하철_노선_수정됨(response, 신분당선);
	}

	/**
	 * When 존재하지 않는 지하철 노선을 수정하면
	 * Then 요청이 실패된다
	 */
	@DisplayName("지하철 노선을 수정할 때 해당 지하철 노선이 없으면 실패한다.")
	@Test
	void updateLineFail() {
		// given

		// when
		var response = 지하철_노선_수정_요청(1L, 경강선_이름, 경강선_색상);

		// then
		지하철_노선이_존재_하지_않아_실패됨(response);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 삭제하면
	 * Then 해당 지하철 노선 정보는 삭제된다
	 */
	@DisplayName("지하철 노선을 삭제한다.")
	@Test
	void deleteLine() {
		// given
		var 신분당선 = 지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선_삭제_요청(신분당선.getId());

		// then
		지하철_노선_삭제됨(response, 신분당선);
	}

	private ExtractableResponse<Response> 지하철_노선_생성_요청() {
		var 신사역 = 지하철역_생성(StationFixture.신사역);
		var 논현역 = 지하철역_생성(StationFixture.신사역);
		return LineFixture.지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 신사역.getId(), 논현역.getId(), 10);
	}

	private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		지하철_노선_목록_조회됨(지하철_노선_목록_조회_요청(), 지하철_노선_리스폰_변환(response));
	}

	private ExtractableResponse<Response> 지하철_노선_중복된_이름으로_생성_요청() {
		return LineFixture.지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 1L, 2L, 10);
	}

	private void 중복된_이름으로_노선_생성이_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.jsonPath().getInt("status"))
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.jsonPath().getString("message"))
			.isEqualTo(LineDuplicationNameException.message);
	}

	private ExtractableResponse<Response> 존재하지_않는_지하철역으로_지하철_노선_생성_요청() {
		return LineFixture.지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 1L, 2L, 10);
	}

	private void 지하철역이_존재_하지_않아_노선_생성이_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(response.jsonPath().getInt("status"))
			.isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(response.jsonPath().getString("message"))
			.isEqualTo(StationNotFoundException.message);
	}

	private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> response, LineResponse... lineResponses) {
		List<LineResponse> responses = response.jsonPath().getList(".", LineResponse.class);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		IntStream.range(0, responses.size())
			.forEach(i -> 지하철_노선_조회됨(지하철_노선_조회_요청(responses.get(i).getId()), lineResponses[i]));
	}

	private void 지하철_노선_조회됨(ExtractableResponse<Response> response, LineResponse expected) {
		LineResponse actual = 지하철_노선_리스폰_변환(response);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	private void 지하철_노선이_존재_하지_않아_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(response.jsonPath().getInt("status"))
			.isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(response.jsonPath().getString("message"))
			.isEqualTo(LineNotFoundException.message);
	}

	private void 지하철_노선_수정됨(ExtractableResponse<Response> response, LineResponse expected) {
		LineResponse findResponse = 지하철_노선_리스폰_변환(지하철_노선_조회_요청(expected.getId()));

		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(findResponse).usingRecursiveComparison()
			.isNotEqualTo(expected);
	}

	private void 지하철_노선_삭제됨(ExtractableResponse<Response> response, LineResponse expected) {
		List<String> lineNames = 지하철_노선_목록_조회_요청().jsonPath().getList("name", String.class);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		assertThat(lineNames).doesNotContain(expected.getName());
	}
}
