package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.steps.LineSteps.*;
import static nextstep.subway.steps.SectionSteps.*;
import static nextstep.subway.steps.StationSteps.createStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
	private Long line4;

	private Long 사당역;
	private Long 금정역;

	private int 서울_사당_거리 = 7;
	private int 사당_금정_거리 = 10;
	private int 금정_중앙_거리 = 7;

	/**
	 * 상행) 서울역 - 사당역 - 대공원역 - 금정역 - 중앙역 (하행
	 * Given 지하철역과 노선 생성을 요청 하고
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();

		사당역 = createStation("사당역").jsonPath().getLong("id");
		금정역 = createStation("금정역").jsonPath().getLong("id");

		line4 = createLine("4호선", "#00A5DE", 사당역, 금정역, 사당_금정_거리).jsonPath().getLong("id");
	}

	/** 지하철 구간 추가 기능 개선 **/
	/**
	 * When 지하철 노선에 새로운 구간 추가를 요청 하면
	 * Then 노선에 새로운 구간이 추가된다
	 */
	@DisplayName("지하철 노선에 구간을 등록")
	@Test
	void addLineSection() {
		// when
		Long 중앙역 = createStation("중앙역").jsonPath().getLong("id");
		createSection(line4, 금정역, 중앙역, 금정_중앙_거리);

		// then
		List<Long> stationIds = getStationIds(showLineById(line4));
		assertThat(stationIds).containsExactly(사당역, 금정역, 중앙역);
	}

	/**
	 * When 기존 상행 종점역을 하행역으로 하는 구간 추가를 요청 하면
	 * Then 지하철 노선 조회 시 등록한 구간을 찾을 수 있다.
	 * */
	@DisplayName("새로운 역을 상행 종점역으로 등록")
	@Test
	void addSectionsFrontStation() {
		// when
		Long 서울역 = createStation("서울역").jsonPath().getLong("id");
		createSection(line4, 서울역, 사당역, 서울_사당_거리);

		// then
		List<Long> stationIds = getStationIds(showLineById(line4));
		assertThat(stationIds).contains(서울역, 사당역, 금정역);

		int totalDistance = showLineById(line4).jsonPath().getInt("distance");
		assertThat(totalDistance).isEqualTo(서울_사당_거리 + 사당_금정_거리);
	}

	/**
	 * When 기존 하행 종점역을 상행역으로 하는 구간 추가를 요청 하면
	 * Then 지하철 노선 조회 시 등록한 구간을 찾을 수 있다.
	 * */
	@DisplayName("새로운 역을 하행 종점으로 등록")
	@Test
	void addSectionsBackStation() {
		// when
		Long 중앙역 = createStation("중앙역").jsonPath().getLong("id");
		createSection(line4, 금정역, 중앙역, 금정_중앙_거리);

		// then
		List<Long> stationIds = getStationIds(showLineById(line4));
		assertThat(stationIds).contains(사당역, 금정역, 중앙역);

		int totalDistance = showLineById(line4).jsonPath().getInt("distance");
		assertThat(totalDistance).isEqualTo(사당_금정_거리 + 금정_중앙_거리);
	}

	/**
	 * When 구간 사이에 새로운 역을 등록 시 길이가 기존 역 사이 길이보다 크거나 같으면
	 * Then 400에러 발생
	 * Then 지하철 노선 조회 시 등록한 구간을 찾을 수 없다.
	 * */
	@DisplayName("구간 등록 시, 기존 역 사이 길이보다 짧아야 한다")
	@Test
	void addStationDistanceShort() {
		// when
		Long 대공원역 = createStation("대공원역").jsonPath().getLong("id");
		ExtractableResponse<Response> createResponse = createSection(line4, 사당역, 대공원역, 사당_금정_거리); // 4

		// then
		assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

		// then
		List<Long> stationIds = getStationIds(showLineById(line4));
		assertThat(stationIds).doesNotContain(대공원역);
	}

	/**
	 * When 구간 등록 시 이미 구간으로 등록되어 있다면
	 * Then 400에러 발생
	 * Then 지하철 노선 조회 시 등록한 구간을 찾을 수 없다.
	 * */
	@DisplayName("구간 등록 시, 중복 등록은 되지 않는다")
	@Test
	void AllExistStation() {
		// when
		ExtractableResponse<Response> createResponse = createSection(line4, 사당역, 금정역, 사당_금정_거리);

		// then
		assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

		// then
		List<Long> stationIds = getStationIds(showLineById(line4));
		assertThat(stationIds).containsOnlyOnce(사당역, 금정역);
	}

	/**
	 * When 구간 등록 시 노선에 등록하려는 역이 하나도 없다면
	 * Then 400에러 발생
	 * Then 지하철 노선 조회 시 등록한 구간을 찾을 수 없다.
	 * */
	@DisplayName("구간 등록 시, 역이 하나는 등록되어 있어야 한다")
	@Test
	void NoExistStation() {
		// when
		Long 대공원역 = createStation("대공원역").jsonPath().getLong("id");
		Long 과천역 = createStation("과천역").jsonPath().getLong("id");
		ExtractableResponse<Response> createResponse = createSection(line4, 대공원역, 과천역, 1);

		// then
		assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

		// then
		List<Long> stationIds = getStationIds(showLineById(line4));
		assertThat(stationIds).doesNotContain(대공원역, 과천역);
	}

	/** 지하철 구간 제거 기능 개선 **/
	/**
	 * Given 지하철 노선에 새로운 구간을 추가한뒤
	 * When 기존 종점 하행역을 삭제하면
	 * Then 1개의 구간만 남는다.
	 */
	@DisplayName("지하철 노선에 구간을 제거")
	@Test
	void deleteMiddleStation() {
		// given
		Long 중앙역 = createStation("중앙역").jsonPath().getLong("id");
		createSection(line4, 금정역, 중앙역, 금정_중앙_거리);

		// when
		ExtractableResponse<Response> deleteResponse = deleteSection(line4, 금정역);
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		// then
		ExtractableResponse<Response> response = showLineById(line4);
		assertAll(
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(사당역, 중앙역),
			() -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(사당_금정_거리 + 금정_중앙_거리)
		);
	}

	/**
	 * When 노선의 마지막 구간을 삭제하면
	 * Then 400에러 발생
	 * Then 기존 구간이 조회된다.
	 */
	@DisplayName("구간 삭제 시, 구간이 1개인 경우 에러 발생")
	@Test
	void deleteOneSection() {
		// when
		ExtractableResponse<Response> deleteResponse = deleteSection(line4, 금정역);
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

		// then
		List<Long> stationIds = getStationIds(showLineById(line4));
		assertThat(stationIds).contains(사당역, 금정역);
	}

	/**
	 * Given 종점 하행역을 추가한 뒤
	 * When 종점 상행역을 삭제하면
	 * Then 추가한 구간만 조회된다.
	 * */
	@DisplayName("구간 삭제 시, 종점 상행역이 삭제되면 다음역이 종점 상행역이 된다")
	@Test
	void deleteFrontStation() {
		// given
		Long 중앙역 = createStation("중앙역").jsonPath().getLong("id");
		createSection(line4, 금정역, 중앙역, 금정_중앙_거리);

		// when
		ExtractableResponse<Response> deleteResponse = deleteSection(line4, 사당역);
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		// then
		ExtractableResponse<Response> response = showLineById(line4);
		assertAll(
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(금정역, 중앙역),
			() -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(금정_중앙_거리)
		);
	}

	/**
	 * Given 종점 하행역을 추가한 뒤
	 * When 종점 하행역을 삭제하면
	 * Then 기존의 구간만 조회된다.
	 * */
	@DisplayName("구간 삭제 시, 종점 하행역이 삭제되면 전역이 종점 하행역이 된다")
	@Test
	void deleteBackStation() {
		// given
		Long 중앙역 = createStation("중앙역").jsonPath().getLong("id");
		createSection(line4, 금정역, 중앙역, 금정_중앙_거리);

		int totalDistance = showLineById(line4).jsonPath().getInt("distance");
		assertThat(totalDistance).isEqualTo(사당_금정_거리 + 금정_중앙_거리);

		// when
		ExtractableResponse<Response> deleteResponse = deleteSection(line4, 중앙역);
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		// then
		ExtractableResponse<Response> response = showLineById(line4);
		assertAll(
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(사당역, 금정역),
			() -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(사당_금정_거리)
		);
	}

	/**
	 * When 등록되어 있지 않는 역을 삭제하면
	 * Then 400에러 발생
	 * Then 기존의 구간만 조회된다.
	 * */
	@DisplayName("구간 삭제 시, 삭제하려는 역이 노선에 등록되어 있어야한다")
	@Test
	void deleteNoExistStation() {
		// given
		Long 중앙역 = createStation("중앙역").jsonPath().getLong("id");
		createSection(line4, 금정역, 중앙역, 금정_중앙_거리);

		int totalDistance = showLineById(line4).jsonPath().getInt("distance");
		assertThat(totalDistance).isEqualTo(사당_금정_거리 + 금정_중앙_거리);

		// when
		Long 서울역 = createStation("서울역").jsonPath().getLong("id");
		ExtractableResponse<Response> deleteResponse = deleteSection(line4, 서울역);
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		// then
		ExtractableResponse<Response> response = showLineById(line4);
		assertAll(
			() -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(사당역, 금정역, 중앙역),
			() -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(사당_금정_거리 + 금정_중앙_거리)
		);
	}
}
