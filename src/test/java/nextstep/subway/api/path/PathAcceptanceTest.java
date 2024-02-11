package nextstep.subway.api.path;

import static nextstep.subway.fixture.SubwayScenarioFixtureCreator.*;
import static nextstep.subway.utils.resthelper.ExtractableResponseParser.*;
import static nextstep.subway.utils.resthelper.PathRequestExecutor.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.api.CommonAcceptanceTest;

/**
 * @author : Rene Choi
 * @since : 2024/02/07
 */
public class PathAcceptanceTest extends CommonAcceptanceTest {

	/**
	 * 최단 경로 조회 - 성공 케이스 1
	 * - given 지하철 역, 노선이 교차로 존재하여 2개 이상의 경로가 존재할 때
	 * - when 두 개의 역에 대한 경로 조회 요청에 대해
	 * - then 최단 경로를 적절히 리턴한다
	 * <p>
	 * 노선도 현황 예시
	 * <p>
	 * 교대역    --- *2호선* ---   강남역
	 * |                        |
	 * *3호선*                   *신분당선*
	 * |                        |
	 * 남부터미널역  --- *3호선* ---   양재
	 * <p>
	 */
	@Test
	@DisplayName("교대역에서 양재역까지의 최단 경로 조회: 지하철 노선에 교대역, 강남역, 남부터미널역, 양재역이 연결되어 있을 때, 교대역에서 양재역까지의 최단 경로와 거리가 정확하게 반환되는지 검증")
	void findShortestPath_success1() {

		// given
		long stationId1 = createStation("교대역");
		long stationId2 = createStation("강남역");
		long stationId3 = createStation("양재역");
		long stationId4 = createStation("남부터미널역");

		long lineId1 = createLine("2호선", stationId1, stationId2, 10L);

		long lineId2 = createLine("3호선", stationId1, stationId3, 5L);
		createSection(lineId2, stationId1, stationId4, 2L);

		long lineId3 = createLine("신분당선", stationId2, stationId3, 10L);

		// when
		ExtractableResponse<Response> findPathResponse = executeFindPathRequest(stationId1, stationId3);

		// then
		assertThat(findPathResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(parseStations(findPathResponse)).extracting("name").containsExactly("교대역", "남부터미널역", "양재역");
		assertThat(parseDistance(findPathResponse)).isEqualTo(5);
	}

	/**
	 * 최단 경로 조회 - 성공 케이스 2
	 * - given 지하철 역, 노선이 복잡하게 교차되어 여러 경로가 존재할 때
	 * - when 두 개의 역에 대한 경로 조회 요청에 대해
	 * - then 가장 짧은 경로를 정확하게 리턴한다
	 * <p>
	 * 노선도 현황 예시
	 * <p>
	 * 서울역         ----- *1호선*  -----   시청역
	 * |                                  |
	 * *경의중앙선*                           *2호선*
	 * |                                  |
	 * 홍대입구역      ----- *경의중앙선* -----  이대역
	 * |                                  |
	 * *공항철도*                            *2호선*
	 * |                                  |
	 * 디지털미디어시티역 ----- *공항철도* -----    공덕역
	 * <p>
	 */
	@Test
	@DisplayName("서울역에서 공덕역까지의 최단 경로 조회: 지하철 노선에 서울역, 시청역, 홍대입구역, 이대역, 디지털미디어시티역, 공덕역이 복잡하게 연결되어 있을 때, 서울역에서 공덕역까지의 최단 경로와 거리가 정확하게 반환되는지 검증")
	void findShortestPath_success2() {

		// given
		long stationId1 = createStation("서울역");
		long stationId2 = createStation("시청역");
		long stationId3 = createStation("홍대입구역");
		long stationId4 = createStation("이대역");
		long stationId5 = createStation("디지털미디어시티역");
		long stationId6 = createStation("공덕역");

		long lineId1 = createLine("1호선", stationId1, stationId2, 10L);

		long lineId2 = createLine("2호선", stationId2, stationId4, 15L);
		createSection(lineId2, stationId4, stationId6, 20L);

		long lineId3 = createLine("경의중앙선", stationId1, stationId3, 5L);
		createSection(lineId3, stationId3, stationId4, 20L);

		long lineId4 = createLine("공항철도", stationId3, stationId6, 5L);
		createSection(lineId4, stationId3, stationId5, 2L);

		// when
		ExtractableResponse<Response> findPathResponse = executeFindPathRequest(stationId1, stationId6);

		// then
		assertThat(findPathResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(parseStations(findPathResponse)).extracting("name").containsExactly("서울역", "홍대입구역", "디지털미디어시티역", "공덕역");
		assertThat(parseDistance(findPathResponse)).isEqualTo(10);
	}

	@Test
	@DisplayName("출발역과 도착역이 같을 때의 예외 상황 처리 검증 1")
	void findPath_Fail_When_SourceAndTargetAreTheSame_1() {
		// given
		long sameStationId = createStation("같은역");

		// when
		ExtractableResponse<Response> response = executeFindPathRequest(sameStationId, sameStationId);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("출발역과 도착역이 같을 때의 예외 상황 처리 검증 2")
	void findPath_Fail_When_SourceAndTargetAreTheSame_2() {
		// given
		long stationId1 = createStation("교대역");
		long stationId2 = createStation("강남역");
		long stationId3 = createStation("남부터미널역");
		createLine("2호선", stationId1, stationId2, 10L);
		createLine("3호선", stationId2, stationId3, 5L);

		// when
		ExtractableResponse<Response> response = executeFindPathRequest(stationId1, stationId1);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("출발역과 도착역이 연결되어 있지 않은 경우에 대한 예외 처리 검증 1 - 시청역과 용산역이 연결되어 있지 않음 ")
	void findPath_Fail_When_StationsAreNotConnected_1() {
		// given
		long stationId1 = createStation("서울역");
		long stationId2 = createStation("시청역");
		long stationId3 = createStation("용산역");
		createLine("1호선", stationId1, stationId2, 10L);

		// when
		ExtractableResponse<Response> response = executeFindPathRequest(stationId2, stationId3);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("출발역과 도착역이 연결되어 있지 않은 경우에 대한 예외 처리 검증 2 - 보다 복잡한 노선 구조에서 서울역과 이대역이 연결되어 있지 않음")
	void findPath_Fail_When_StationsAreNotConnected_2() {
		// given
		long stationId1 = createStation("서울역");
		long stationId2 = createStation("시청역");
		long stationId3 = createStation("홍대입구역");
		long stationId4 = createStation("신촌역");
		long stationId5 = createStation("이대역");
		createLine("1호선", stationId1, stationId2, 10L);
		createLine("2호선", stationId3, stationId4, 5L);
		createLine("경의중앙선", stationId4, stationId5, 3L);

		// when
		ExtractableResponse<Response> response = executeFindPathRequest(stationId1, stationId5);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("존재하지 않는 출발역 또는 도착역 조회 시 예외 처리 검증 1 - 두 역 모두 존재하지 않는 경우 ")
	void findPath_Fail_When_StationDoesNotExist_1() {
		// given
		long nonExistentStationId1 = 99999L; // 존재하지 않는 역 ID
		long nonExistentStationId2 = 99998L; // 또 다른 존재하지 않는 역 ID

		// when
		ExtractableResponse<Response> responseWithNonExistentSource = executeFindPathRequest(nonExistentStationId1, nonExistentStationId2);

		// then
		assertThat(responseWithNonExistentSource.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("존재하지 않는 출발역 또는 도착역 조회 시 예외 처리 검증 2 - 출발역만 존재하는 경우")
	void findPath_Fail_When_StationDoesNotExist_2() {
		// given
		long existentStationId = createStation("서울역");
		long nonExistentStationId = 99999L; // 존재하지 않는 역 ID

		// when
		ExtractableResponse<Response> response = executeFindPathRequest(existentStationId, nonExistentStationId);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("존재하지 않는 출발역 또는 도착역 조회 시 예외 처리 검증 3 - 도착역만 존재하는 경우")
	void findPath_Fail_When_StationDoesNotExist_3() {
		// given
		long nonExistentStationId = 99997L; // 존재하지 않는 역 ID
		long existentStationId = createStation("강남역");

		// when
		ExtractableResponse<Response> response = executeFindPathRequest(nonExistentStationId, existentStationId);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

}
