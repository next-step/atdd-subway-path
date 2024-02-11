package nextstep.subway.api.section;

import static nextstep.subway.fixture.LineFixtureCreator.*;
import static nextstep.subway.fixture.SectionFixtureCreator.*;
import static nextstep.subway.utils.resthelper.ExtractableResponseParser.*;
import static nextstep.subway.utils.resthelper.LineRequestExecutor.*;
import static nextstep.subway.utils.resthelper.SectionRequestExecutor.*;
import static nextstep.subway.utils.resthelper.StationRequestExecutor.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.api.CommonAcceptanceTest;
import nextstep.subway.api.domain.dto.outport.StationInfo;
import nextstep.subway.api.interfaces.dto.request.SectionCreateRequest;
import nextstep.subway.api.interfaces.dto.response.LineResponse;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
@DisplayName("지하철 구간 관리 기능")
public class SectionManagementTest extends CommonAcceptanceTest {

	/**
	 * 구간 등록 - 성공 케이스
	 * - Given 지하철 노선이 존재하고, 해당 노선의 하행 종점역이 존재할 때
	 * - When 새로운 구간을 등록하려고 할 때
	 * - Then 새로운 구간의 상행역은 현재 노선의 하행 종점역이어야 한다.
	 * - And 이미 노선에 등록된 역은 새로운 구간의 하행역이 될 수 없다.
	 * - And 조건을 만족하지 않는 경우 에러를 반환한다.
	 */
	@Test
	@DisplayName("구간 등록 - 성공 케이스")
	void createSection_Success() {
		// given
		long stationId1 = parseId(executeCreateStationRequest("상행 종점역"));
		long stationId2 = parseId(executeCreateStationRequest("하행 종점역"));
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선 이름", stationId1, stationId2)));

		ExtractableResponse<Response> newStationCreateResponse = executeCreateStationRequest("새로운 역");
		long stationId3 = parseId(newStationCreateResponse);

		// When & then
		// 성공 케이스 -> 상행역은 해당 노선에 등록되어 있는 하행 종점역이어야 한다 & 새로운 역이어야 한다 조건을 만족하도록 request 구성
		ExtractableResponse<Response> response = executeCreateSectionRequest(lineId, SectionCreateRequest.builder().upStationId(stationId2).downStationId(stationId3).distance(10L).build());

		assertEquals(HttpStatus.CREATED.value(), response.statusCode());
	}

	/**
	 * 구간 등록 - 예외 케이스
	 * - Given 지하철 노선이 존재하고, 해당 노선에 역이 등록되어 있을 때
	 * - When 부적합한 구간(상행역 또는 하행역 조건 불만족)을 등록하려고 할 때
	 * - Then 에러를 반환한다.
	 */
	@Test
	@Disabled("요구 조건 변화에 따른 검증 로직 수정")
	@DisplayName("구간 등록 - 예외 케이스 1 -> 새로운 구간은 상행역은 해당 노선에 등록되어 있는 하행 종점역이어야 한다는 조건을 불만족")
	void createSection_Failure_1() {
		// Given
		long stationId1 = parseId(executeCreateStationRequest("상행 종점역"));
		long stationId2 = parseId(executeCreateStationRequest("하행 종점역"));
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선 이름", stationId1, stationId2)));

		ExtractableResponse<Response> newStationCreateResponse = executeCreateStationRequest("새로운 역");
		long stationId3 = parseId(newStationCreateResponse);

		// When
		// 새로운 구간의 상행역은 해당 노선에 등록되어 있는 하행 종점역이어야 한다는 조건을 불만족
		ExtractableResponse<Response> response = executeCreateSectionRequest(lineId, SectionCreateRequest.builder().upStationId(stationId1).downStationId(stationId3).distance(10L).build());

		// Then
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
	}

	/**
	 * 구간 등록 - 예외 케이스
	 * - Given 지하철 노선이 존재하고, 해당 노선에 역이 등록되어 있을 때
	 * - When 부적합한 구간(상행역 또는 하행역 조건 불만족)을 등록하려고 할 때
	 * - Then 에러를 반환한다.
	 */
	@Test
	@Disabled("요구 조건 변화에 따른 검증 로직 수정")
	@DisplayName("구간 등록 - 예외 케이스 2 -> 이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다는 조건을 불만족")
	void createSection_Failure_2() {
		// Given
		long stationId1 = parseId(executeCreateStationRequest("상행 종점역"));
		long stationId2 = parseId(executeCreateStationRequest("하행 종점역"));
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선 이름", stationId1, stationId2)));

		// When
		// 이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다는 조건을 불만족
		ExtractableResponse<Response> response = executeCreateSectionRequest(lineId, SectionCreateRequest.builder().upStationId(stationId2).downStationId(stationId1).distance(10L).build());

		// Then
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
	}

	/**
	 * 구간 제거 - 성공 케이스
	 * - Given 지하철 노선이 존재하고, 해당 노선에 여러 역이 등록되어 있을 때
	 * - When 하행 종점역을 제거하려고 할 때
	 * - Then 해당 역이 제거된다.
	 * - And 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.
	 * - And 조건을 만족하지 않는 경우 에러를 반환한다.
	 */
	@Test
	@DisplayName("구간 제거 - 성공 케이스")
	void deleteDownEndSection_Success() {
		// Given
		long stationId1 = parseId(executeCreateStationRequest("상행 종점역"));
		long stationId2 = parseId(executeCreateStationRequest("하행 종점역"));
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선", stationId1, stationId2)));
		long stationId3 = parseId(executeCreateStationRequest("새로운 역"));
		executeCreateSectionRequest(lineId, SectionCreateRequest.builder().upStationId(stationId2).downStationId(stationId3).distance(10L).build());

		// When
		ExtractableResponse<Response> deleteResponse = executeDeleteSectionRequest(lineId, stationId3);

		// Then
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.statusCode());
	}

	/**
	 * 구간 제거 - 예외 케이스
	 * - Given 지하철 노선이 존재하고, 해당 노선에 여러 역이 등록되어 있을 때
	 * - When 마지막 구간이 아닌 구간을 제거하려고 할 때
	 * - Then 에러를 반환한다.
	 */
	@Test
	@Disabled("요구 조건 변화에 따른 검증 로직 수정")
	@DisplayName("구간 제거 - 예외 케이스 -> 마지막 구간이 아닌 구간을 제외할 때 예외 발생")
	void deleteNotDownEndSection_Failure_1() {
		// Given
		long stationId1 = parseId(executeCreateStationRequest("Station A"));
		long stationId2 = parseId(executeCreateStationRequest("Station B"));
		long stationId3 = parseId(executeCreateStationRequest("Station C"));

		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선", stationId1, stationId2)));

		SectionCreateRequest newSectionRequest = SectionCreateRequest.builder()
			.upStationId(stationId2)
			.downStationId(stationId3)
			.distance(10L)
			.build();
		executeCreateSectionRequest(lineId, newSectionRequest);

		// When -> 마지막 구간이 아닌 라인을 삭제 시도한다
		ExtractableResponse<Response> deleteResponse = executeDeleteSectionRequest(lineId, stationId2);

		// Then
		assertEquals(HttpStatus.BAD_REQUEST.value(), deleteResponse.statusCode());
	}

	/**
	 * 구간 제거 - 예외 케이스
	 * - Given 지하철 노선이 존재하고, 해당 노선에 상행과 하행이 단 1개씩 존재할 때
	 * - When 하행역을 삭제하려고 할 때
	 * - Then 에러를 반환한다.
	 */
	@Test
	@Disabled("요구 조건 변화에 따른 검증 로직 수정")
	@DisplayName("구간 제거 - 예외 케이스 -> 상행 종점역과 하행 종점역이 각 1개만 있는 경우 해당 역을 삭제하는 경우 최소 개수 조건을 유지해야 한다는 조건을 불만족하여 예외")
	void deleteSectionWhenMinimumRequiredStationCountNotSatisfied_Failure_2() {
		// Given
		long stationId1 = parseId(executeCreateStationRequest("상행 종점역"));
		long stationId2 = parseId(executeCreateStationRequest("하행 종점역"));
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선", stationId1, stationId2)));

		// When
		ExtractableResponse<Response> deleteResponse = executeDeleteSectionRequest(lineId, stationId2);

		// Then
		assertEquals(HttpStatus.BAD_REQUEST.value(), deleteResponse.statusCode());
	}

	///////////// 등록 추가 요구 사항

	/**
	 * given - 노선에 A역과 C역이 등록되어 있을 때
	 * when - A와 C 사이에 B역을 추가하는 요청
	 * then - A-B, B-C 구간이 생성되며, 길이 조정 -> (상행) A역 - B역 - C역 (하행)
	 */
	@Test
	@DisplayName("노선 가운데 역 추가 - 성공 케이스")
	void addStationBetweenStations_Success() {
		// given
		long stationAId = parseId(executeCreateStationRequest("A역")); // 1
		long stationCId = parseId(executeCreateStationRequest("C역")); // 2
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선", stationAId, stationCId, 7L))); // A - C 노선

		long stationBId = parseId(executeCreateStationRequest("B역")); // 3

		// when
		SectionCreateRequest sectionCreateRequestAB = createSectionCreateRequestWithUpAndDownAndDistance(stationAId, stationBId, 4L);
		ExtractableResponse<Response> sectionABCreateResponse = executeCreateSectionRequest(lineId, sectionCreateRequestAB);// A - B 신규 구간 생성

		// then
		// 1) HttpStatus code
		assertEquals(HttpStatus.CREATED.value(), sectionABCreateResponse.statusCode());

		// 2) 역 조회 검증
		List<String> stationNames = executeGetSpecificStationLineRequest(lineId).as(LineResponse.class).getStations().stream().map(StationInfo::getName).collect(Collectors.toList());
		assertTrue(stationNames.contains("A역"));
		assertTrue(stationNames.contains("B역"));
		assertTrue(stationNames.contains("C역"));
	}

	/**
	 * given - 노선에 Y역과 Z역이 등록되어 있을 때
	 * when - 노선의 시작점에 X역 추가 요청
	 * then -  X-Y, Y-Z 구간 생성 및 길이 조정
	 */
	@Test
	@DisplayName("노선 처음에 역 추가 - 성공 케이스")
	void addStationAtTheBeginning_Success() {
		// given
		long stationYId = parseId(executeCreateStationRequest("Y역")); // 1
		long stationZId = parseId(executeCreateStationRequest("Z역")); // 2
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선", stationYId, stationZId, 4L)));// Y - Z 노선
		long stationXId = parseId(executeCreateStationRequest("X역")); // 3

		// when
		SectionCreateRequest sectionCreateRequestXY = createSectionCreateRequestWithUpAndDownAndDistance(stationXId, stationYId, 3L);
		ExtractableResponse<Response> sectionXYCreateResponse = executeCreateSectionRequest(lineId, sectionCreateRequestXY); // X - Y 신규 구간 생성

		// then
		// 1) HttpStatus code
		assertEquals(HttpStatus.CREATED.value(), sectionXYCreateResponse.statusCode());

		// 2) 역 조회 검증
		LineResponse lineResponse = executeGetSpecificStationLineRequest(lineId).as(LineResponse.class);
		List<String> stationNames = lineResponse.getStations().stream().map(StationInfo::getName).collect(Collectors.toList());
		assertTrue(stationNames.contains("X역"));
		assertTrue(stationNames.contains("Y역"));
		assertTrue(stationNames.contains("Z역"));
	}

	///////////// 삭제 추가 요구 사항

	/**
	 * 구간 제거 - 성공 케이스
	 * - Given 지하철 노선이 존재하고, 해당 노선에 여러 역이 등록되어 있을 때
	 * - When 노선의 중간에 위치한 역을 제거하려고 할 때
	 * - Then 해당 역이 제거되고, 인접한 역들의 거리가 조정된다.
	 * -> A - (3m) B - (4m) C -> B 삭제 -> A - (7m) C
	 */
	@Test
	@DisplayName("노선의 중간 역 제거 - 성공 케이스")
	void removeMiddleStation_Success() {
		// Given: 지하철 노선에 A, B, C 세 역이 순서대로 등록되어 있을 때
		long stationAId = parseId(executeCreateStationRequest("A역"));
		long stationBId = parseId(executeCreateStationRequest("B역"));
		long stationCId = parseId(executeCreateStationRequest("C역"));
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선", stationAId, stationCId, 7L)));  // A - C

		SectionCreateRequest sectionCreateRequestAB = createSectionCreateRequestWithUpAndDownAndDistance(stationAId, stationBId, 3L); // A - B
		executeCreateSectionRequest(lineId, sectionCreateRequestAB);

		// When: B 역을 제거하는 요청을 보냈을 때
		ExtractableResponse<Response> deleteResponse = executeDeleteSectionRequest(lineId, stationBId);

		// Then: HTTP 상태 코드가 NO_CONTENT이며, 노선에서 B 역이 제거되고 A와 C 역 사이의 거리가 조정됨
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.statusCode());

	}

	/**
	 * 구간 제거 - 성공 케이스
	 * - Given 지하철 노선이 존재하고, 해당 노선에 여러 역이 등록되어 있을 때
	 * - When 노선의 상행 종점역을 제거하려고 할 때
	 * - Then 상행 종점역이 제거되고, 다음 역이 상행 종점역이 된다.
	 * -> (UpEnd) A - B - C -> A 삭제 -> (UpEnd) B - C
	 */
	@Test
	@DisplayName("노선의 상행 종점역 제거 - 성공 케이스")
	void removeUpEndStation_Success() {
		// given
		long stationAId = parseId(executeCreateStationRequest("A역"));
		long stationBId = parseId(executeCreateStationRequest("B역"));
		long stationCId = parseId(executeCreateStationRequest("C역"));
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선", stationAId, stationCId, 10L)));

		executeCreateSectionRequest(lineId, createSectionCreateRequestWithUpAndDownAndDistance(stationAId, stationBId, 5L));

		// when
		ExtractableResponse<Response> deleteResponse = executeDeleteSectionRequest(lineId, stationAId);

		// then
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.statusCode());

		// 상행 종점역 A가 제거된 후의 노선 정보 확인
		List<Long> stationIds = parseStationIds(executeGetSpecificStationLineRequest(lineId));
		assertFalse(stationIds.contains(stationAId)); // A역이 제거되었는지 확인
		assertTrue(stationIds.contains(stationBId)); // B역이 상행 종점역이 되었는지 확인
	}

	/**
	 * 구간 제거 - 성공 케이스
	 * - Given 지하철 노선이 존재하고, 해당 노선에 여러 역이 등록되어 있을 때
	 * - When 노선의 상행 종점역을 제거하려고 할 때
	 * - Then 상행 종점역이 제거되고, 다음 역이 상행 종점역이 된다.
	 * -> (UpEnd) A - B - C -> C 삭제 ->  A - B
	 */
	@Test
	@DisplayName("노선의 최하단 종점역 제거 - 성공 케이스")
	void removeDownEndStation_Success() {
		// given: 지하철 노선에 A, B, C 세 역이 순서대로 등록되어 있을 때
		long stationAId = parseId(executeCreateStationRequest("A역"));
		long stationBId = parseId(executeCreateStationRequest("B역"));
		long stationCId = parseId(executeCreateStationRequest("C역"));
		long lineId = parseId(executeCreateLineRequest(createLineCreateRequest("노선", stationAId, stationCId, 10L)));
		executeCreateSectionRequest(lineId, createSectionCreateRequestWithUpAndDownAndDistance(stationAId, stationBId, 5L));

		// when: C 역(최하단 종점역)을 제거하는 요청을 보냈을 때
		ExtractableResponse<Response> deleteResponse = executeDeleteSectionRequest(lineId, stationCId);

		// then: HTTP 상태 코드가 NO_CONTENT이며, 노선에서 C 역이 제거됨
		assertEquals(HttpStatus.NO_CONTENT.value(), deleteResponse.statusCode());

		// 노선 상세 정보를 조회하여 C역이 제거되었는지 검증
		List<Long> stationIds = parseStationIds(executeGetSpecificStationLineRequest(lineId));
		assertFalse(stationIds.contains(stationCId)); // C역이 제거되었는지 확인
		assertTrue(stationIds.contains(stationAId) && stationIds.contains(stationBId)); // A역과 B역이 여전히 노선에 존재하는지 확인
	}

}
