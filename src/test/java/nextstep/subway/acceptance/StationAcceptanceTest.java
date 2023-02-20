package nextstep.subway.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.steps.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStationTest() {
        // when
		String stationName = "강남역";
		createStation(stationName);

        // then
		List<String> stationNames = showStation().jsonPath().getList("name", String.class);
		assertThat(stationNames).containsAnyOf(stationName);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
		String stationName1 = "강남역";
		String stationName2 = "교대역";

		createStation(stationName1);
		createStation(stationName2);

		// when
		List<String> stationNames = showStation().jsonPath().getList("name", String.class);

		// then
		assertThat(stationNames).hasSize(2).containsAnyOf(stationName1, stationName2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
		// given
		String stationName = "강남역";
		long id = createStation(stationName).jsonPath().getLong("id");

		// when
		deleteStationById(id);

		// then
		List<String> stationNames = showStation().jsonPath().getList("name", String.class);
		assertThat(stationNames).doesNotContain(stationName);
	}
}
