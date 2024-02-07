package subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static subway.utils.enums.Location.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpMethod;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.AcceptanceTest;
import subway.dto.line.LineResponse;
import subway.dto.line.LineUpdateRequest;
import subway.dto.section.SectionRequest;
import subway.dto.station.StationDTO;
import subway.dto.station.StationResponse;
import subway.fixture.LineFixture;
import subway.fixture.SectionRequestFixture;
import subway.fixture.StationFixture;
import subway.utils.rest.Rest;

class LineAcceptanceTest extends AcceptanceTest {

	/**
	 * When 지하철 노선을 생성하면
	 * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
	 */
	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void saveLine() {
		// when
		ExtractableResponse<Response> savedLine = LineFixture.builder()
			.build()
			.create();
		LineResponse actualResponse = savedLine.as(LineResponse.class);

		String uri = LINES.path(actualResponse.getId()).toUriString();
		LineResponse expectedResponse = Rest.builder().get(uri).as(LineResponse.class);

		// then
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
	}

	/**
	 * Given 2개의 지하철 노선을 생성하고
	 * When 지하철 노선 목록을 조회하면
	 * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
	 */
	@DisplayName("지하철 노선 2개를 생성하고, 생성된 목록을 조회한다.")
	@Test
	void createLineAndRetrieveLines() {
		// given
		LineFixture lineFixture1 = LineFixture.builder().build();
		LineFixture lineFixture2 = LineFixture.builder().upStationName("남강역").downStationName("재양역").build();
		LineResponse expectedLineResponse1 = lineFixture1.actionReturnLineResponse();
		LineResponse expectedLineResponse2 = lineFixture2.actionReturnLineResponse();

		// when
		List<LineResponse> actualResponse = Rest.builder()
			.get(LINES.path())
			.jsonPath()
			.getList("", LineResponse.class);

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
	void createLineAndRetrieveLine() {
		// given
		LineResponse expectedResponse = LineFixture.builder()
			.build()
			.actionReturnLineResponse();

		// when
		String uri = LINES.path(expectedResponse.getId()).toUriString();
		ExtractableResponse<Response> extractableResponse = Rest.builder().get(uri);
		LineResponse actualResponse = extractableResponse.as(LineResponse.class);

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
	void updateLine() {
		// given
		String expectedName = "변경된 이름";
		String expectedColor = "변경된 색깔";
		LineResponse lineResponse = LineFixture.builder()
			.build()
			.actionReturnLineResponse();

		// when
		LineUpdateRequest request = new LineUpdateRequest(expectedName, expectedColor);

		String uri = LINES.path(lineResponse.getId()).toUriString();
		Rest.builder().uri(uri).body(request).put();
		LineResponse actualResponse = Rest.builder().get(uri).as(LineResponse.class);

		// then
		assertThat(actualResponse.getName()).isEqualTo(expectedName);
		assertThat(actualResponse.getColor()).isEqualTo(expectedColor);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 삭제하면
	 * Then 해당 지하철 노선 정보는 삭제된다
	 */
	@DisplayName("지하철 노선을 생성하고, 삭제한다.")
	@Test
	void deleteLine() {
		// given
		LineResponse lineResponse = LineFixture.builder()
			.build()
			.actionReturnLineResponse();

		// when
		String uri = LINES.path(lineResponse.getId()).toUriString();
		Rest.builder().delete(uri);

		// then
		List<LineResponse> actualResponse = Rest.builder()
			.get(LINES.path())
			.jsonPath()
			.getList("", LineResponse.class);

		assertThat(actualResponse).isEmpty();
	}

	/**
	 * Given 해당 노선에 구간을 추가하고
	 * When 노선을 조회 하면
	 * Then 구간이 추가된 노선을 응답 받을수 있다.
	 */
	@DisplayName("노선 구간 추가")
	@TestFactory
	Stream<DynamicTest> addSection() {
		// given
		LineResponse givenLineResponse = LineFixture.builder()
			.build()
			.actionReturnLineResponse();

		long addedStationId = StationFixture.builder()
			.stationName("추가된 정류장")
			.build()
			.create()
			.jsonPath()
			.getLong("id");

		return Stream.of(
			dynamicTest("구간 추가를 위해 필요한 노선을 만든다.", () -> {
				// then
				String uri = LINES.path(givenLineResponse.getId()).toUriString();
				LineResponse actualLineResponse = Rest.builder()
					.get(uri)
					.as(LineResponse.class);
				assertThat(actualLineResponse).usingRecursiveComparison().isEqualTo(givenLineResponse);
			}),
			dynamicTest("추가할 구간에 필요한 정류장을 생성한다.", () -> {
				// then
				String uri = STATIONS.path();
				List<StationResponse> stationResponse = Rest.builder()
					.get(uri)
					.jsonPath()
					.getList("", StationResponse.class);
				assertThat(stationResponse).extracting(StationResponse::getId).contains(addedStationId);
			}),
			dynamicTest("입력 받은 구간을 등록한다.", () -> {
				// when
				Long finalStationId = getFinalStationId(givenLineResponse);

				SectionRequest.Builder sectionRequestFixture = SectionRequestFixture.builder()
					.upStationId(finalStationId)
					.downStationId(addedStationId);

				String addSectionPostUri = LINES.path(givenLineResponse.getId()).path("/sections").toUriString();
				LineResponse expectedLineResponse = Rest.builder()
					.uri(addSectionPostUri)
					.body(sectionRequestFixture.build())
					.post()
					.as(LineResponse.class);

				// then
				String linesGetUri = LINES.path(givenLineResponse.getId()).toUriString();
				LineResponse actualLineResponse = Rest.builder().get(linesGetUri).as(LineResponse.class);
				assertThat(actualLineResponse).usingRecursiveComparison().isEqualTo(expectedLineResponse);
			}),
			dynamicTest("추가할려는 구간의 정류장이 존재하지 않는 경우 exception 테스트",
				() -> {
					SectionRequest exceptionRequestFixture =
						SectionRequestFixture.builder()
							.upStationId(Long.MAX_VALUE)
							.downStationId(Long.MAX_VALUE)
							.build();

					String uri = LINES.path(givenLineResponse.getId()).path("/sections").toUriString();
					Rest.builder()
						.uri(uri)
						.body(exceptionRequestFixture)
						.checkException("EntityNotFoundException", "존재하지 않는 정류장입니다.", HttpMethod.POST);
				}),
			dynamicTest("이미 노선에 포함된 정류장을 추가 하려고 하는 경우 Exception 테스트",
				() -> {
					Long finalStationId = getFinalStationId(givenLineResponse);
					SectionRequest exceptionRequestFixture =
						SectionRequestFixture.builder()
							.upStationId(finalStationId)
							.downStationId(finalStationId)
							.build();

					String uri = LINES.path(givenLineResponse.getId()).path("/sections").toUriString();
					Rest.builder()
						.uri(uri)
						.body(exceptionRequestFixture)
						.checkException("IllegalArgumentException", "추가 할려는 정류장은 이미 해당 노선에 존재하는 정류장입니다.",
							HttpMethod.POST);
				}),
			dynamicTest("추가할려는 상행역이 마지막 정류장이 아닐 경우 Exception 테스트",
				() -> {
					long addedExceptionStationId = StationFixture.builder()
						.stationName("비정상 테스트 정류장")
						.build()
						.create()
						.jsonPath()
						.getLong("id");

					SectionRequest exceptionRequestFixture =
						SectionRequestFixture.builder()
							.upStationId(addedExceptionStationId)
							.downStationId(addedExceptionStationId)
							.build();

					String uri = LINES.path(givenLineResponse.getId()).path("/sections").toUriString();
					Rest.builder()
						.uri(uri)
						.body(exceptionRequestFixture)
						.checkException("IllegalArgumentException", "해당 노선의 마지막 정류장이 아닙니다.", HttpMethod.POST);
				})
		);
	}

	private Long getFinalStationId(LineResponse lineResponse) {
		int size = lineResponse.getStations().size();
		return lineResponse.getStations().get(size - 1).getId();
	}

	@DisplayName("노선 구간 삭제")
	@TestFactory
	Stream<DynamicTest> deleteSection() {
		// given
		LineResponse givenLineResponse = LineFixture.builder()
			.build()
			.actionReturnLineResponse();

		String linesGetUri = LINES.path(givenLineResponse.getId()).toUriString();
		LineResponse initLine = Rest.builder()
			.get(linesGetUri)
			.as(LineResponse.class);

		long addedStationId = StationFixture.builder()
			.stationName("추가된 정류장")
			.build()
			.create()
			.jsonPath()
			.getLong("id");

		return Stream.of(
			dynamicTest("구간 삭제를 위해 필요한 노선을 만든다.", () -> {
				// then
				assertThat(initLine).usingRecursiveComparison().isEqualTo(givenLineResponse);
			}),
			dynamicTest("해당 노선에 정류장이 2개 밖에 없을 경우 Exception", () -> {
				String uri = LINES.path(givenLineResponse.getId())
					.path("/sections")
					.queryParam("stationId", "2")
					.toUriString();

				Rest.builder()
					.uri(uri)
					.body(Map.of("", ""))
					.checkException("IllegalArgumentException", "해당 노선은 두개의 정류장만 존재 하므로, 삭제할 수 없습니다.",
						HttpMethod.DELETE);
			}),
			dynamicTest("삭제할 구간에 필요한 정류장을 추가한다.", () -> {
				// when
				Long finalStationId = getFinalStationId(givenLineResponse);
				SectionRequest.Builder sectionRequestFixture = SectionRequestFixture.builder()
					.upStationId(finalStationId)
					.downStationId(addedStationId);

				String addSectionPostUri = LINES.path(givenLineResponse.getId()).path("/sections").toUriString();
				LineResponse expectedLineResponse = Rest.builder()
					.uri(addSectionPostUri)
					.body(sectionRequestFixture.build())
					.post()
					.as(LineResponse.class);

				// then
				String linesGetUri1 = LINES.path(givenLineResponse.getId()).toUriString();
				LineResponse actualLineResponse = Rest.builder().get(linesGetUri1).as(LineResponse.class);
				assertThat(actualLineResponse).usingRecursiveComparison().isEqualTo(expectedLineResponse);
			}),
			dynamicTest("삭제 할려는 정류장이 마지막 정류장이 아닌 경우 Exception", () -> {
				Long firstStationId = initLine.getStations()
					.stream()
					.map(StationDTO::getId)
					.findFirst()
					.orElse(1L);

				String uri = LINES.path(givenLineResponse.getId())
					.path("/sections")
					.queryParam("stationId", firstStationId)
					.toUriString();

				Rest.builder()
					.uri(uri)
					.body(Map.of("", ""))
					.checkException("IllegalArgumentException", "해당 노선의 마지막 정류장이 아닙니다.", HttpMethod.DELETE);
			}),
			dynamicTest("노선의 구간(마지막 정류장)을 삭제한다.", () -> {
				String uri = LINES.path(givenLineResponse.getId())
					.path("/sections")
					.queryParam("stationId", addedStationId)
					.toUriString();

				Rest.builder().delete(uri);

				String uris = LINES.path(givenLineResponse.getId()).toUriString();
				LineResponse actualLineResponse = Rest.builder()
					.get(uris)
					.as(LineResponse.class);

				assertThat(actualLineResponse).usingRecursiveComparison().isEqualTo(initLine);
			})
		);
	}
}
