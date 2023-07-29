package nextstep.subway.acceptance;

import static nextstep.subway.utils.StationFixture.강남역_이름;
import static nextstep.subway.utils.StationFixture.양재역_이름;
import static nextstep.subway.utils.StationFixture.지하철역_삭제_요청;
import static nextstep.subway.utils.StationFixture.지하철역_생성_요청;
import static nextstep.subway.utils.StationFixture.지하철역_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

	/**
	 * When 지하철역을 생성하면
	 * Then 지하철역이 생성된다
	 * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
	 */
	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// when
		var response = 지하철역_생성_요청(강남역_이름);

		// then
		생성된_지하철역을_검증한다(response);

		// then
		지하철역_목록_조회시_생성된_역을_검증한다();
	}

	/**
	 * Given 2개의 지하철역을 생성하고
	 * When 지하철역 목록을 조회하면
	 * Then 2개의 지하철역을 응답 받는다
	 */
	// TODO: 지하철역 목록 조회 인수 테스트 메서드 생성
	@DisplayName("지하철역 목록을 조회한다.")
	@Test
	void showStations() {
		// given
		지하철역들을_생성한다();

		// when
		var response = 지하철역_조회_요청();

		// then
		지하철역_목록_조회시_생성된_역을_검증한다(response);
	}

	/**
	 * Given 지하철역을 생성하고
	 * When 그 지하철역을 삭제하면
	 * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
	 */
	// TODO: 지하철역 삭제 인수 테스트 메서드 생성
	@DisplayName("지하철역을 삭제한다.")
	@Test
	void deleteStation() {
		// given
		지하철역_생성_요청(강남역_이름);

		// when
		var response = 지하철역을_삭제한다();

		// then
		삭제된_지하철역을_검증한다(response);
	}

	private void 생성된_지하철역을_검증한다(ExtractableResponse<Response> response) {
		String name = response.jsonPath().getString("name");

		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(name).isEqualTo(강남역_이름);
	}

	private void 지하철역_목록_조회시_생성된_역을_검증한다() {
		역_이름들을_검증한다(지하철역_조회_요청(), 강남역_이름);
	}

	private void 지하철역들을_생성한다() {
		지하철역_생성_요청(강남역_이름);
		지하철역_생성_요청(양재역_이름);
	}

	private void 지하철역_목록_조회시_생성된_역을_검증한다(ExtractableResponse<Response> response) {
		역_이름들을_검증한다(response, 강남역_이름, 양재역_이름);
	}

	private void 역_이름들을_검증한다(ExtractableResponse<Response> response, String... stationNames) {
		List<String> names = response.jsonPath().getList("name", String.class);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(names).containsAnyOf(stationNames);
	}

	private ExtractableResponse<Response> 지하철역을_삭제한다() {
		return 지하철역_삭제_요청(1L);
	}

	private void 삭제된_지하철역을_검증한다(ExtractableResponse<Response> response) {
		List<String> names = 지하철역_조회_요청().jsonPath().getList("name", String.class);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		assertThat(names).doesNotContain(강남역_이름);
	}
}
