package atdd.station;

import atdd.HttpTestHelper;
import atdd.station.model.dto.PathResponseView;
import atdd.station.model.dto.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
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

    private StationDto stationDto;
    private StationDto stationDto2;
    private StationDto stationDto3;
    private StationDto stationDto4;
    private StationDto stationDto5;

    private StationDto stationDto11;
    private StationDto stationDto12;
    private StationDto stationDto13;
    private StationDto stationDto14;
    private StationDto stationDto15;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        this.httpTestHelper = new HttpTestHelper(webTestClient);
        this.pathTestHelper = new PathTestHelper(httpTestHelper);
        this.stationTestHelper = new StationTestHelper(webTestClient);
        this.lineTestHelper = new LineTestHelper(webTestClient);

        stationDto = stationTestHelper.createStation(STATION_NAME);
        stationDto2 = stationTestHelper.createStation(STATION_NAME_2);
        stationDto3 = stationTestHelper.createStation(STATION_NAME_3);
        stationDto4 = stationTestHelper.createStation(STATION_NAME_4);
        stationDto5 = stationTestHelper.createStation(STATION_NAME_5);

        stationDto11 = stationTestHelper.createStation(STATION_NAME_11);
        stationDto12 = stationTestHelper.createStation(STATION_NAME_12);
        stationDto13 = stationTestHelper.createStation(STATION_NAME_13);
        stationDto14 = stationTestHelper.createStation(STATION_NAME_14);
        stationDto15 = stationTestHelper.createStation(STATION_NAME_15);

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
        lineTestHelper.addEdge(lineId2, stationDto15.getId(), stationDto4.getId(), 10, 10);
    }

    @Test
    public void findPath() {
        //when
        EntityExchangeResult result = pathTestHelper.findPath(stationDto11.getId(), stationDto4.getId());
        PathResponseView responseView = (PathResponseView) result.getResponseBody();

        //then
        assertThat(result.getResponseHeaders().getETag()).isNotNull();

        assertThat(responseView.getStations().size()).isEqualTo(6);
        assertThat(responseView.getStations().get(0).getName()).isEqualTo(STATION_NAME_11);
        assertThat(responseView.getStations().get(1).getName()).isEqualTo(STATION_NAME_12);
        assertThat(responseView.getStations().get(2).getName()).isEqualTo(STATION_NAME);
        assertThat(responseView.getStations().get(3).getName()).isEqualTo(STATION_NAME_2);
        assertThat(responseView.getStations().get(4).getName()).isEqualTo(STATION_NAME_3);
        assertThat(responseView.getStations().get(5).getName()).isEqualTo(STATION_NAME_4);
    }

    @Test
    public void findShortTimePath() {
        //when
        EntityExchangeResult result = pathTestHelper.findShortTimePath(stationDto14.getId(), stationDto4.getId());
        PathResponseView responseView = (PathResponseView) result.getResponseBody();

        //then
        assertThat(result.getResponseHeaders().getETag()).isNotNull();

        assertThat(responseView.getStations().size()).isEqualTo(3);
        assertThat(responseView.getStations().get(0).getName()).isEqualTo(STATION_NAME_14);
        assertThat(responseView.getStations().get(1).getName()).isEqualTo(STATION_NAME_15);
        assertThat(responseView.getStations().get(2).getName()).isEqualTo(STATION_NAME_4);
    }

    @Test
    void findPathETag() {
        // given
        EntityExchangeResult result = pathTestHelper.findPath(stationDto11.getId(), stationDto4.getId());
        String eTag = result.getResponseHeaders().getETag();

        // when
        EntityExchangeResult eTagResult = pathTestHelper.findPath(stationDto11.getId(), stationDto4.getId(), eTag);

        //then
        assertThat(eTagResult.getStatus()).isEqualTo(HttpStatus.NOT_MODIFIED);
    }
}
