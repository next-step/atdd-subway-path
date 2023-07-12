package subway.acceptance.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import subway.acceptance.AcceptanceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        final String stationName = "강남역";
        var createStationResponse = StationSteps.역_생성_API(stationName);

        // then
        assertThat(createStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        var retrieveStationsResponse = StationSteps.역_목록_조회_API();
        assertThat(retrieveStationsResponse.jsonPath().getList("name", String.class)).containsAnyOf(stationName);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void retrieveStations() {
        // given
        final List<String> createStationNames = List.of("강남역", "역삼역");
        final int createdStations = createStationNames.size();
        createStationNames.forEach(StationSteps::역_생성_API);

        // when
        var retrieveStationsResponse = StationSteps.역_목록_조회_API();
        assertThat(retrieveStationsResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        assertThat(retrieveStationsResponse.body().jsonPath().getList("$").size()).isEqualTo(createdStations);

    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void removeStation() {
        // given
        final String stationName = "강남역";
        var createResponse = StationSteps.역_생성_API(stationName);
        final String createdLocation = createResponse.header("Location");
        final Integer createdId = createResponse.body().jsonPath().get("id");

        // when
        var deletedStation = StationSteps.역_제거_API(createdLocation);
        assertThat(deletedStation.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        var retrieveStationsResponse = StationSteps.역_목록_조회_API();
        assertThat(retrieveStationsResponse.body().jsonPath().getList("id")).doesNotContain(createdId);

    }

}