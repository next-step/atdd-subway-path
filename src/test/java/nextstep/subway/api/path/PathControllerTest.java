package nextstep.subway.api.path;

import static nextstep.subway.fixture.LineFixtureCreator.*;
import static nextstep.subway.utils.resthelper.ExtractableResponseParser.*;
import static nextstep.subway.utils.resthelper.LineRequestExecutor.*;
import static nextstep.subway.utils.resthelper.PathRequestExecutor.*;
import static nextstep.subway.utils.resthelper.SectionRequestExecutor.*;
import static nextstep.subway.utils.resthelper.StationRequestExecutor.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.api.CommonAcceptanceTest;
import nextstep.subway.api.interfaces.dto.request.SectionCreateRequest;
import nextstep.subway.api.interfaces.dto.response.PathResponse;

/**
 * @author : Rene Choi
 * @since : 2024/02/07
 */
public class PathControllerTest extends CommonAcceptanceTest {

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
	 * <p>
	 * 요청 예시
	 * HTTP/1.1 200
	 * Request method:	GET
	 * Request URI:	http://localhost:55494/paths?source=1&target=3
	 * Headers: 	Accept=application/json
	 * Content-Type=application/json; charset=UTF-8
	 * <p>
	 * 응답 예시
	 * <p>
	 * HTTP/1.1 200
	 * Content-Type: application/json
	 * Transfer-Encoding: chunked
	 * Date: Sat, 09 May 2020 14:54:11 GMT
	 * Keep-Alive: timeout=60
	 * Connection: keep-alive
	 * <p>
	 * {
	 * "stations": [
	 * {
	 * "id": 1,
	 * "name": "교대역"
	 * },
	 * {
	 * "id": 4,
	 * "name": "남부터미널역"
	 * },
	 * {
	 * "id": 3,
	 * "name": "양재역"
	 * }
	 * ],
	 * "distance": 5
	 * }
	 */
	@Test
	@DisplayName("최단 경로 조회 - 성공 케이스 1")
	void findShortestPath_success1() {

		// given
		ExtractableResponse<Response> createStationResponse1 = executeCreateStationRequest("교대역");
		long stationId1 = parseId(createStationResponse1);
		ExtractableResponse<Response> createStationResponse2 = executeCreateStationRequest("강남역");
		long stationId2 = parseId(createStationResponse2);
		ExtractableResponse<Response> createStationResponse3 = executeCreateStationRequest("양재역");
		long stationId3 = parseId(createStationResponse3);
		ExtractableResponse<Response> createStationResponse4 = executeCreateStationRequest("남부터미널역");
		long stationId4 = parseId(createStationResponse4);

		ExtractableResponse<Response> createLineResponse1 = executeCreateLineRequest(createLineCreateRequest("2호선", stationId1, stationId2, 10L));
		long lineId1 = parseId(createLineResponse1);

		ExtractableResponse<Response> createLineResponse2 = executeCreateLineRequest(createLineCreateRequest("3호선", stationId1, stationId3, 5L));
		long lineId2 = parseId(createLineResponse2);
		SectionCreateRequest sectionCreateRequest = SectionCreateRequest.builder().upStationId(stationId1).downStationId(stationId4).distance(2L).build();
		ExtractableResponse<Response> response = executeCreateSectionRequest(lineId2, sectionCreateRequest);

		ExtractableResponse<Response> createLineResponse3 = executeCreateLineRequest(createLineCreateRequest("신분당선", stationId2, stationId3, 10L));
		long lineId3 = parseId(createLineResponse3);

		// when
		ExtractableResponse<Response> findPathResponse = executeFindPathRequest(stationId1, stationId3);

		// then
		assertThat(findPathResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(findPathResponse.as(PathResponse.class).getStations()).extracting("name").containsExactly("교대역", "남부터미널역", "양재역");
		assertThat(findPathResponse.as(PathResponse.class).getDistance()).isEqualTo(5);
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
	 * <p>
	 * 요청 예시
	 * HTTP/1.1 200
	 * Request method:	GET
	 * Request URI:	http://localhost:55494/paths?source=1&target=6
	 * Headers: 	Accept=application/json
	 * Content-Type=application/json; charset=UTF-8
	 * <p>
	 * 응답 예시
	 * <p>
	 * HTTP/1.1 200
	 * Content-Type: application/json
	 * Transfer-Encoding: chunked
	 * Date: Sat, 09 May 2020 14:54:11 GMT
	 * Keep-Alive: timeout=60
	 * Connection: keep-alive
	 * <p>
	 * {
	 * "stations": [
	 * {
	 * "id": 1,
	 * "name": "서울역"
	 * },
	 * {
	 * "id": 3,
	 * "name": "홍대입구역"
	 * },
	 * {
	 * "id": 5,
	 * "name": "디지털미디어시티역"
	 * },
	 * {
	 * "id": 6,
	 * "name": "공덕역"
	 * }
	 * ],
	 * "distance": 20
	 * }
	 */
	@Test
	@DisplayName("최단 경로 조회 - 성공 케이스 2")
	void findShortestPath_success2() {

		// given
		ExtractableResponse<Response> createStationResponse1 = executeCreateStationRequest("서울역");
		long stationId1 = parseId(createStationResponse1);
		ExtractableResponse<Response> createStationResponse2 = executeCreateStationRequest("시청역");
		long stationId2 = parseId(createStationResponse2);
		ExtractableResponse<Response> createStationResponse3 = executeCreateStationRequest("홍대입구역");
		long stationId3 = parseId(createStationResponse3);
		ExtractableResponse<Response> createStationResponse4 = executeCreateStationRequest("이대역");
		long stationId4 = parseId(createStationResponse4);
		ExtractableResponse<Response> createStationResponse5 = executeCreateStationRequest("디지털미디어시티역");
		long stationId5 = parseId(createStationResponse5);
		ExtractableResponse<Response> createStationResponse6 = executeCreateStationRequest("공덕역");
		long stationId6 = parseId(createStationResponse6);

		ExtractableResponse<Response> createLineResponse1 = executeCreateLineRequest(createLineCreateRequest("1호선", stationId1, stationId2, 10L));
		long lineId1 = parseId(createLineResponse1);

		ExtractableResponse<Response> createLineResponse2 = executeCreateLineRequest(createLineCreateRequest("2호선", stationId2, stationId4, 15L));
		long lineId2 = parseId(createLineResponse2);
		executeCreateSectionRequest(lineId2, SectionCreateRequest.builder().upStationId(stationId2).downStationId(stationId6).distance(20L).build());

		ExtractableResponse<Response> createLineResponse3 = executeCreateLineRequest(createLineCreateRequest("경의중앙선", stationId1, stationId3, 5L));
		long lineId3 = parseId(createLineResponse3);
		executeCreateSectionRequest(lineId3, SectionCreateRequest.builder().upStationId(stationId3).downStationId(stationId4).distance(20L).build());

		ExtractableResponse<Response> createLineResponse4 = executeCreateLineRequest(createLineCreateRequest("공항철도", stationId3, stationId6, 5L));
		long lineId4 = parseId(createLineResponse4);
		executeCreateSectionRequest(lineId4, SectionCreateRequest.builder().upStationId(stationId3).downStationId(stationId5).distance(2L).build());

		// when
		ExtractableResponse<Response> findPathResponse = executeFindPathRequest(stationId1, stationId6);

		// then
		assertThat(findPathResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(findPathResponse.as(PathResponse.class).getStations()).extracting("name").containsExactly("서울역", "홍대입구역", "디지털미디어시티역", "공덕역");
		assertThat(findPathResponse.as(PathResponse.class).getDistance()).isEqualTo(10);
	}

}
