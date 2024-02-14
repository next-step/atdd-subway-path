package subway.acceptance.line;

import static org.assertj.core.api.Assertions.*;
import static subway.fixture.acceptance.LineAcceptanceSteps.*;
import static subway.fixture.acceptance.StationAcceptanceSteps.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import subway.acceptance.AcceptanceTest;
import subway.dto.line.LineResponse;

class LineAcceptanceTest extends AcceptanceTest {

	/**
	 * When 지하철 노선을 생성하면
	 * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
	 */
	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void successSaveLine() {
		// when
		LineResponse actualResponse = 노선_생성();

		// then
		LineResponse expectedResponse = 노선_조회(actualResponse.getId());
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
	}

	/**
	 * Given 2개의 지하철 노선을 생성하고
	 * When 지하철 노선 목록을 조회하면
	 * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
	 */
	@DisplayName("지하철 노선 2개를 생성하고, 생성된 목록을 조회한다.")
	@Test
	void successCreateLineAndRetrieveLines() {
		// given
		Long 강남역 = 정류장_생성_ID_반환("강남역");
		Long 양재역 = 정류장_생성_ID_반환("양재역");
		Long 남강역 = 정류장_생성_ID_반환("남강역");
		Long 재양역 = 정류장_생성_ID_반환("재양역");
		LineResponse expectedLineResponse1 = 노선_생성(강남역, 양재역);
		LineResponse expectedLineResponse2 = 노선_생성(남강역, 재양역);

		// when
		List<LineResponse> actualResponse = 모든_노선_조회();

		// then
		assertThat(actualResponse.get(0)).usingRecursiveComparison().isEqualTo(expectedLineResponse1);
		assertThat(actualResponse.get(1)).usingRecursiveComparison().isEqualTo(expectedLineResponse2);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 조회하면
	 * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
	 */
	@DisplayName("생성된 노선을 조회한다.")
	@Test
	void successCreateLineAndRetrieveLine() {
		// given
		LineResponse expectedResponse = 노선_생성();

		// when
		LineResponse actualResponse = 노선_조회(expectedResponse.getId());

		// then
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 수정하면
	 * Then 해당 지하철 노선 정보는 수정된다
	 */
	@DisplayName("지하철 노선을 생성하고, 생성된 노선을 수정한다.")
	@Test
	void successUpdateLine() {
		// given
		String actualName = "변경된 이름";
		String actualColor = "변경된 색깔";
		LineResponse lineResponse = 노선_생성();

		// when
		노선_수정(lineResponse.getId(), actualName, actualColor);

		// then
		LineResponse expectedResponse = 노선_조회(lineResponse.getId());
		assertThat(actualName).isEqualTo(expectedResponse.getName());
		assertThat(actualColor).isEqualTo(expectedResponse.getColor());
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 삭제하면
	 * Then 해당 지하철 노선 정보는 삭제된다
	 */
	@DisplayName("지하철 노선을 생성하고, 삭제한다.")
	@Test
	void successDeleteLine() {
		// given
		LineResponse lineResponse = 노선_생성();

		// when
		노선_삭제(lineResponse.getId());

		// then
		List<LineResponse> actualResponse = 모든_노선_조회();
		assertThat(actualResponse).isEmpty();
	}

	/**
	 * given 노선을 생성한다.
	 * when 노선의 처음 부분에 정류장을 추가한다.
	 * then 추가된 정류장을 응답 받을 수 있다.
	 */
	@DisplayName("생성된 노선의 처음 부분에 정류장을 추가한다.")
	@Test
	void successAddSection1() {
		// given
		LineResponse 노선_생성 = 노선_생성();
		Long upStationId = 정류장_생성_ID_반환("논현역");
		Long downStationId = 노선_생성.getStations().get(0).getId();

		// when
		LineResponse 노선_구간_추가 = 노선_구간_추가(노선_생성.getId(), upStationId, downStationId, 10);

		// then
		LineResponse 노선_조회 = 노선_조회(노선_생성.getId());
		assertThat(노선_구간_추가).usingRecursiveComparison().isEqualTo(노선_조회);
	}

	/**
	 * given 노선을 생성한다.
	 * when 노선의 처음 부분에 정류장을 추가한다.
	 * then 추가된 정류장을 응답 받을 수 있다.
	 */
	@DisplayName("생성된 노선의 중간 부분에 정류장을 추가한다.")
	@Test
	void successAddSection() {
		// given
		LineResponse 노선_생성 = 노선_생성();
		Long upStationId = 노선_생성.getStations().get(0).getId();
		Long downStationId = 정류장_생성_ID_반환("논현역");

		// when
		LineResponse 노선_구간_추가 = 노선_구간_추가(노선_생성.getId(), upStationId, downStationId, 10);

		// then
		LineResponse 노선_조회 = 노선_조회(노선_생성.getId());
		assertThat(노선_구간_추가).usingRecursiveComparison().isEqualTo(노선_조회);
	}
}
