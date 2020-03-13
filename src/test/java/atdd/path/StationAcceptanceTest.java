package atdd.path;

import atdd.BaseAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends BaseAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);
    private StationHttpTest stationHttpTest;

    @BeforeEach
    void setUp() {
        stationHttpTest = new StationHttpTest(webTestClient);
    }

    @Test
    void createStation() {
        //when
        Long stationId = stationHttpTest.createStation("강남");

        //then
        assertThat(stationId).isEqualTo(1L);
    }
}
