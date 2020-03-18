package atdd.station;

import atdd.HttpTestHelper;
import atdd.station.model.dto.PathResponseView;
import atdd.station.model.dto.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static atdd.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PathAcceptanceTest {
    private HttpTestHelper httpTestHelper;

    private PathTestHelper pathTestHelper;
    private StationTestHelper stationTestHelper;
    private LineTestHelper lineTestHelper;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        this.httpTestHelper = new HttpTestHelper(webTestClient);
        this.pathTestHelper = new PathTestHelper(httpTestHelper);
        this.stationTestHelper = new StationTestHelper(webTestClient);
        this.lineTestHelper = new LineTestHelper(webTestClient);
    }

    @Test
    public void findPath() {
        //given
        StationDto stationDto = stationTestHelper.createStation(STATION_NAME); // 강남
        StationDto stationDto2 = stationTestHelper.createStation(STATION_NAME_2);// 역삼
        StationDto stationDto3 = stationTestHelper.createStation(STATION_NAME_3); //선릉
        StationDto stationDto4 = stationTestHelper.createStation(STATION_NAME_4); // 삼성
        StationDto stationDto5 = stationTestHelper.createStation(STATION_NAME_5); //종합운동장

        StationDto stationDto11 = stationTestHelper.createStation(STATION_NAME_11); // 고속터미널
        StationDto stationDto12 = stationTestHelper.createStation(STATION_NAME_12); // 교대
        StationDto stationDto13 = stationTestHelper.createStation(STATION_NAME_13); // 남부터미
        StationDto stationDto14 = stationTestHelper.createStation(STATION_NAME_14); // 매봉
        StationDto stationDto15 = stationTestHelper.createStation(STATION_NAME_15); // 도곡

        long lineId = lineTestHelper.createLine(CREATE_LINE_REQUEST_VIEW_1).getId();
        long lineId2 = lineTestHelper.createLine(CREATE_LINE_REQUEST_VIEW_3).getId();

        lineTestHelper.addEdge(lineId, stationDto12.getId(), stationDto.getId(), 10, 10);
        lineTestHelper.addEdge(lineId, stationDto.getId(), stationDto2.getId(), 10, 10);
        lineTestHelper.addEdge(lineId, stationDto2.getId(), stationDto3.getId(), 10, 10);
        lineTestHelper.addEdge(lineId, stationDto3.getId(), stationDto4.getId(), 10, 10);
        lineTestHelper.addEdge(lineId, stationDto4.getId(), stationDto5.getId(), 10, 10);

        lineTestHelper.addEdge(lineId2, stationDto11.getId(), stationDto12.getId(), 10, 10);
        lineTestHelper.addEdge(lineId2, stationDto12.getId(), stationDto13.getId(), 10, 10);
        lineTestHelper.addEdge(lineId2, stationDto13.getId(), stationDto14.getId(), 10, 10);
        lineTestHelper.addEdge(lineId2, stationDto14.getId(), stationDto15.getId(), 10, 10);

        //when
        PathResponseView responseView = pathTestHelper.findPath(stationDto11.getId(), stationDto4.getId());

        //then
        assertThat(responseView.getStations().size()).isEqualTo(6);
        assertThat(responseView.getStations().get(0).getName()).isEqualTo(STATION_NAME_11);
        assertThat(responseView.getStations().get(1).getName()).isEqualTo(STATION_NAME_12);
        assertThat(responseView.getStations().get(2).getName()).isEqualTo(STATION_NAME);
        assertThat(responseView.getStations().get(3).getName()).isEqualTo(STATION_NAME_2);
        assertThat(responseView.getStations().get(4).getName()).isEqualTo(STATION_NAME_3);
        assertThat(responseView.getStations().get(5).getName()).isEqualTo(STATION_NAME_4);
    }
}
