package atdd.station.controller;

import atdd.station.AbstractAcceptanceTest;
import atdd.station.dto.path.PathFindResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import static atdd.station.fixture.SubwayLineFixture.getFirstSubwayLine;
import static atdd.station.fixture.SubwayLineFixture.getSecondSubwayLine;
import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends AbstractAcceptanceTest {
    private static final String PATH_API_BASE_URL = "/paths";

    private RestWebClientTest restWebClientTest;

    @BeforeEach
    void setUp() {
        this.restWebClientTest = new RestWebClientTest(this.webTestClient);
    }

    @DisplayName("지하철_간의_최단거리를_구한다")
    @Test
    void findShortestPath() {
        restWebClientTest.creatSubwayLine(getSecondSubwayLine());
        restWebClientTest.creatSubwayLine(getFirstSubwayLine());

        long startStationId = 0L;
        long endStationId = 2L;

        //when
        EntityExchangeResult<PathFindResponseDto> expectResponse
                = restWebClientTest.getMethodAcceptance
                (PATH_API_BASE_URL + "?startId=" + startStationId + "&endId=" + endStationId, PathFindResponseDto.class);

        PathFindResponseDto path = expectResponse.getResponseBody();

        //then
        assertThat(path.getStartStationId()).isEqualTo(0L);
        assertThat(path.getEndStationId()).isEqualTo(2L);
        assertThat(path.getStations().size()).isEqualTo(3);
    }
}
