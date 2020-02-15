package atdd.station.controller;

import atdd.station.AbstractAcceptanceTest;
import atdd.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import static atdd.station.fixture.StationFixture.KANGNAM_STATION_NAME;
import static atdd.station.fixture.StationFixture.PANGYO_STATION_NAME;
import static org.assertj.core.api.Assertions.assertThat;

public class GraphAcceptanceTest extends AbstractAcceptanceTest {
    private static final String PATH_API_BASE_URL = "/paths/";

    private RestWebClientTest restWebClientTest;

    @BeforeEach
    void setUp() {
        this.restWebClientTest = new RestWebClientTest(this.webTestClient);
    }

    @DisplayName("강남역_지하철_등록을_요청이_성공하는지")
    @ParameterizedTest
    @ValueSource(strings = {KANGNAM_STATION_NAME, PANGYO_STATION_NAME})
    void createStationSuccessTest(String stationName) {
        long startStationId = 1L;
        long endStationId = 5L;

        //when
        EntityExchangeResult<Station> expectResponse
                = restWebClientTest.getMethodAcceptance
                (PATH_API_BASE_URL + "?startId=" + startStationId + "endId=" + endStationId, PathFindResponseDto.class);

        PathFindResponseDto path = expectResponse.getResponseBody();

        //then
        assertThat(expectResponse.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(path.getStartStation()).isEqualTo(1L);
        assertThat(path.getEndStation()).isEqualTo(5L);
        assertThat(path.getStations().length()).isEqualTo(4);
    }
}
