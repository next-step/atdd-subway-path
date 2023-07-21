package nextstep.subway.acceptance;

import static nextstep.subway.utils.LineFixture.*;
import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.SectionDuplicationStationException;
import nextstep.subway.exception.SectionRemoveLastStationException;
import nextstep.subway.exception.SectionRemoveSizeException;
import nextstep.subway.utils.LineFixture;

public class SectionAcceptanceTest extends AcceptanceTest {

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선에 구간을 추가하면
	 * Then 지하철 노선 조회 시 생성된 구간을 찾을 수 있다
	 */
	@DisplayName("지하철 노선에 구간을 등록한다.")
	@Test
	void addSection() {
		// given
		var 신분당선 = 지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선에_구간_생성_요청(신분당선);

		// then
		지하철_노선에_구간_생성됨(response, "신사역", "논현역", "신논현역");
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 새로운 역을 기존 역 사이로 구간을 추가하면
	 * Then 지하철 노선 목록 조회 시 생성 및 수정된 구간을 찾을 수 있다.
	 */
	@DisplayName("지하철 노선에 새로운 역을 기존 역 사이로 구간을 추가한다.")
	@Test
	void addSection2() {
		// given
		지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선에_새로운_역을_기존_역_사이로_구간_추가_요청();

		// then
		지하철_노선에_구간_생성됨(response, 신사역, 신논현역, 논현역);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 새로운 역을 상행 종점으로 구간을 생성하면
	 * Then 지하철 노선 목록 조회 시 생성 및 수정된 구간을 찾을 수 있다.
	 */
	@DisplayName("지하철 노선에 새로운 역을 상행 종점으로 구간을 생성한다.")
	@Test
	void addSection3() {
		// given
		지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선에_새로운_역을_상행_종점으로_구간_추가_요청();

		// then
		지하철_노선에_구간_생성됨(response, 신논현역, 신사역, 논현역);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 새로운 역을 하행 종점으로 구간을 생성하면
	 * Then 지하철 노선 목록 조회 시 생성 및 수정된 구간을 찾을 수 있다.
	 */
	@DisplayName("지하철 노선에 새로운 역을 하행 종점으로 구간을 생성한다.")
	@Test
	void addSection4() {
		// given
		지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선에_새로운_역을_하행_종점으로_구간_추가_요청();

		// then
		지하철_노선에_구간_생성됨(response, 신사역, 논현역, 신논현역);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 지하철 노선에 하행역이 중복된 역인 구간을 생성하면
	 * Then 요청이 실패된다
	 */
	@DisplayName("지하철 노선에 구간을 생성할 때 새로운 구간의 하행역이 중복된 역이라면 실패한다.")
	@Test
	void createSectionFail2() {
		// given
		var 신분당선 = 지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선에_하행역이_중복된_구간_추가_요청(신분당선);

		// then
		새로운_구간의_하행역이_중복된_역이면_실패됨(response);
	}

	/**
	 * Given 자하철 노선에 구간을 포함하여 생성하고
	 * When 지하철 구간을 삭제하면
	 * Then 지하철 노선 조회 시 삭제한 구간을 찾을 수 없다
	 */
	@DisplayName("지하철 노선에 구간을 삭제한다.")
	@Test
	void deleteSection() {
		// given
		var 신분당선 = 지하철_구간을_포함한_노선_생성();

		// when
		var response = 지하철_노선의_마지막_구간_삭제_요청(신분당선);

		// then
		지하철_노선의_구간이_삭제됨(response, 신분당선);
	}

	/**
	 * Given 지하철 노선에 구간을 포함하여 생성하고
	 * When 마지막 구간이 아닌 구간을 삭제하면
	 * Then 요청이 실패된다
	 */
	@DisplayName("지하철 노선에 구간을 삭제할 떄 마지막 구간이 아닌 경우 실패한다.")
	@Test
	void deleteSectionFail() {
		// given
		var 신분당선 = 지하철_구간을_포함한_노선_생성();

		// when
		var response = 지하철_노선의_중간_구간_삭제_요청(신분당선);

		// then
		마지막_구간이_아닌_경우_실패됨(response);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 구간이 1개 일 때 구간을 삭제하면
	 * Then 요청이 실패된다
	 */
	@DisplayName("지하철 노선에 구간을 삭제할 때 구간이 1개인 경우 실패한다.")
	@Test
	void deleteSectionFail2() {
		// given
		var 신분당선 = 지하철_신분당선_노선_생성();

		// when
		var response = 지하철_노선의_마지막_구간_삭제_요청(신분당선);

		// then
		구간이_1개인_경우_실패됨(response);
	}

	private ExtractableResponse<Response> 지하철_노선에_새로운_역을_기존_역_사이로_구간_추가_요청() {
		지하철역_생성_요청(신논현역);
		return 지하철_노선의_구간_추가_요청(1L, 1L, 3L, 5);
	}

	private ExtractableResponse<Response> 지하철_노선에_새로운_역을_상행_종점으로_구간_추가_요청() {
		지하철역_생성_요청(신논현역);
		return 지하철_노선의_구간_추가_요청(1L, 3L, 1L, 5);
	}

	private ExtractableResponse<Response> 지하철_노선에_새로운_역을_하행_종점으로_구간_추가_요청() {
		지하철역_생성_요청(신논현역);
		return 지하철_노선의_구간_추가_요청(1L, 2L, 3L, 5);
	}

	private void 지하철_노선에_구간_생성됨(ExtractableResponse<Response> response, String... expectStationNames) {
		LineResponse expected = 지하철_노선_리스폰_변환(response);
		List<String> stationNames = 지하철_노선_조회_요청(expected.getId()).jsonPath().getList("stations.name", String.class);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(stationNames).contains(expectStationNames);
	}

	private ExtractableResponse<Response> 지하철_노선에_구간_생성_요청(LineResponse lineResponse) {
		var upStation = lineResponse.getStations().get(1);
		var downStation = 지하철역_생성(신논현역);
		return LineFixture.지하철_노선의_구간_추가_요청(lineResponse.getId(), upStation.getId(), downStation.getId(), 10);
	}

	private void 새로운_구간의_하행역이_중복된_역이면_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.jsonPath().getInt("status"))
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.jsonPath().getString("message"))
			.isEqualTo(SectionDuplicationStationException.MESSAGE);
	}

	private ExtractableResponse<Response> 지하철_노선에_하행역이_중복된_구간_추가_요청(LineResponse lineResponse) {
		var upStation = lineResponse.getStations().get(1);
		var downStation = lineResponse.getStations().get(0);
		return 지하철_노선의_구간_추가_요청(lineResponse.getId(), upStation.getId(), downStation.getId(), 10);
	}

	private void 지하철_노선의_구간이_삭제됨(ExtractableResponse<Response> response, LineResponse expected) {
		LineResponse actual = 지하철_노선_리스폰_변환(지하철_노선_조회_요청(expected.getId()));

		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		assertThat(actual).usingRecursiveComparison()
			.isNotEqualTo(expected);
	}

	private ExtractableResponse<Response> 지하철_노선의_마지막_구간_삭제_요청(LineResponse lineResponse) {
		var stations = lineResponse.getStations();
		StationResponse removeStation = stations.get(stations.size() - 1);
		return 지하철_노선의_구간_삭제_요청(lineResponse.getId(), removeStation.getId());
	}

	private ExtractableResponse<Response> 지하철_노선의_중간_구간_삭제_요청(LineResponse response) {
		List<StationResponse> stations = response.getStations();
		StationResponse removeStation = stations.get(stations.size() - 2);
		return 지하철_노선의_구간_삭제_요청(response.getId(), removeStation.getId());
	}

	private void 구간이_1개인_경우_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.jsonPath().getInt("status"))
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.jsonPath().getString("message"))
			.isEqualTo(SectionRemoveSizeException.MESSAGE);
	}

	private void 마지막_구간이_아닌_경우_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.jsonPath().getInt("status"))
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.jsonPath().getString("message"))
			.isEqualTo(SectionRemoveLastStationException.MESSAGE);
	}
}
