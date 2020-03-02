package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.StationResponseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AbstractAcceptanceTest {
    private String STATION_NAME = "사당역";
    private StationHttpTest stationHttpTest;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
    }

    @DisplayName("지하철역 등록")
    @Test
    public void createStation() {
        // when
        Long stationId = stationHttpTest.createStation(STATION_NAME);

        // then
        EntityExchangeResult<StationResponseView> response = stationHttpTest.retrieveStation(stationId);
        assertThat(response.getResponseBody().getName()).isEqualTo(STATION_NAME);
    }
}
