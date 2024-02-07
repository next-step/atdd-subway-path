package subway.station;

import static org.assertj.core.api.Assertions.*;
import static subway.utils.enums.Location.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.AcceptanceTest;
import subway.fixture.StationFixture;
import subway.utils.rest.Rest;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

	/**
	 * When 지하철역을 생성하면
	 * Then 지하철역이 생성된다
	 * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
	 */
	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// when
		String expectedStationName = StationFixture.builder()
			.build()
			.create()
			.jsonPath()
			.getString("name");

		// then
		List<String> actualStationNames = Rest.builder()
			.get(STATIONS.path())
			.jsonPath()
			.getList("name", String.class);
		assertThat(actualStationNames).containsAnyOf(expectedStationName);
	}

	/**
	 * Given 2개의 지하철역을 생성하고
	 * When 지하철역 목록을 조회하면
	 * Then 2개의 지하철역을 응답 받는다
	 */
	@DisplayName("지하철역 2개를 생성하고, 생성된 2개의 지하철역을 확인한다.")
	@Test
	void createTwoStation() {
		// given
		String 강남역 = createStation("강남역").jsonPath().getString("name");
		String 양재역 = createStation("양재역").jsonPath().getString("name");

		// when
		ExtractableResponse<Response> response = Rest.builder().get(STATIONS.path());

		List<String> stationNames = response.jsonPath().getList("name", String.class);

		// then
		assertThat(stationNames).containsAnyOf(강남역, 양재역);
	}

	private ExtractableResponse<Response> createStation(String stationName) {
		return StationFixture.builder()
			.stationName(stationName)
			.build()
			.create();
	}

	/**
	 * Given 지하철역을 생성하고
	 * When 그 지하철역을 삭제하면
	 * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
	 */
	@DisplayName("생성된 지하철역을 삭제 한다.")
	@Test
	void deleteStation() {
		// given
		Long stationId = StationFixture.builder().build()
			.create()
			.jsonPath()
			.getLong("id");

		// when
		String uri = STATIONS.path(stationId).toUriString();
		Rest.builder().delete(uri);

		// then
		List<Long> stationIds = Rest.builder()
			.get(STATIONS.path())
			.jsonPath()
			.getList("", Long.class);
		assertThat(stationIds).isEmpty();
	}
}
