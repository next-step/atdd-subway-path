package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.E2ETestInitializer;
import nextstep.subway.utils.station.StationManager;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends E2ETestInitializer {

    public static final int ALL_STATIONS_COUNT = 3;
    public static final int ONE_REMOVE_STATIONS_COUNT = 2;
    public static final String FIRST_STATION_NAME = "지하철역이름";
    public static final String SECOND_STATION_NAME = "새로운지하철역이름";
    public static final String THIRD_STATION_NAME = "또다른지하철역이름";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = StationManager.save(FIRST_STATION_NAME);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        response = StationManager.findAll();
        List<String> stationNames = response.jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf(FIRST_STATION_NAME);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    // TODO: 지하철역 목록 조회 인수 테스트 메서드 생성
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void findAllStations() {
        // Given
        StationManager.save(FIRST_STATION_NAME);
        StationManager.save(SECOND_STATION_NAME);
        StationManager.save(THIRD_STATION_NAME);

        // 조회
        ExtractableResponse<Response> response = StationManager.findAll();

        List<StationResponse> stationResponses = response.jsonPath().getList(".", StationResponse.class);
        assertThat(stationResponses.size()).isEqualTo(ALL_STATIONS_COUNT);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    // TODO: 지하철역 제거 인수 테스트 메서드 생성
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // Given
        long firstStationId = StationManager.save(FIRST_STATION_NAME).jsonPath().getLong("id");
        StationManager.save(SECOND_STATION_NAME);
        StationManager.save(THIRD_STATION_NAME);

        // When
        StationManager.delete(firstStationId);

        // then
        ExtractableResponse<Response> response = StationManager.findAll();

        List<StationResponse> stationResponses = response.jsonPath().getList(".", StationResponse.class);
        assertThat(stationResponses.size()).isEqualTo(ONE_REMOVE_STATIONS_COUNT);
    }
}
