package atdd.station;

import atdd.station.model.dto.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private final String STATION_NAME1 = "강남역";
    private final String STATION_NAME2 = "역삼역";
    private final String STATION_NAME3 = "선릉역";

    private StationTestHelper stationTestHelper;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.stationTestHelper = new StationTestHelper(webTestClient);
    }

    @Test
    public void createStation() {
        //when
        StationDto station = stationTestHelper.createStation(STATION_NAME1);

        // then
        assertThat(station.getName()).isEqualTo(STATION_NAME1);
    }

    @Test
    public void findAllStations() {
        // given
        StationDto stations1 = stationTestHelper.createStation(STATION_NAME1);
        StationDto stations2 = stationTestHelper.createStation(STATION_NAME2);
        StationDto stations3 = stationTestHelper.createStation(STATION_NAME3);

        // when
        List<StationDto> stations = stationTestHelper.findAll();

        //then
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).getName()).isEqualTo(STATION_NAME1);
        assertThat(stations.get(1).getName()).isEqualTo(STATION_NAME2);
        assertThat(stations.get(2).getName()).isEqualTo(STATION_NAME3);
    }

    @Test
    public void findStation() {
        // given
        StationDto stations1 = stationTestHelper.createStation(STATION_NAME1);
        StationDto stations2 = stationTestHelper.createStation(STATION_NAME2);
        StationDto stations3 = stationTestHelper.createStation(STATION_NAME3);

        // when
        StationDto station = stationTestHelper.findById(stations1.getId());

        // then
        assertThat(station.getId()).isEqualTo(stations1.getId());
        assertThat(station.getName()).isEqualTo(STATION_NAME1);
    }

    @Test
    public void deleteStation() {
        // given
        StationDto stations1 = stationTestHelper.createStation(STATION_NAME1);
        StationDto stations2 = stationTestHelper.createStation(STATION_NAME2);
        StationDto stations3 = stationTestHelper.createStation(STATION_NAME3);

        // when
        stationTestHelper.deleteById(stations1.getId());

        // then
        List<StationDto> stations = stationTestHelper.findAll();

        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations.get(0).getName()).isNotEqualTo(STATION_NAME1);
        assertThat(stations.get(1).getName()).isNotEqualTo(STATION_NAME1);
    }
}
