package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.StationResponseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AbstractAcceptanceTest {
    private String STATION_NAME = "사당역";
    private StationHttpTest stationHttpTest;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
    }

    @DisplayName("지하철역 등록하기")
    @Test
    public void create() {
        // when
        StationResponseView responseView = stationHttpTest.create(STATION_NAME);

        // then
        assertThat(responseView.getId()).isEqualTo(1L);
    }
}
