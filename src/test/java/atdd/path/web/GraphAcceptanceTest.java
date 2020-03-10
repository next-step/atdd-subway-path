package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.application.dto.StationResponseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalTime;

import static atdd.path.TestConstant.*;

public class GraphAcceptanceTest extends AbstractAcceptanceTest {
    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;
    private EdgeHttpTest edgeHttpTest;

    public static final LocalTime START_TIME = LocalTime.of(5, 00);
    public static final LocalTime END_TIME = LocalTime.of(23, 50);
    public static final int INTERVAL_TIME = 10;
    public static LineRequestView lineRequestView = LineRequestView.builder()
            .name(LINE_NAME)
            .startTime(START_TIME)
            .endTime(END_TIME)
            .interval(INTERVAL_TIME)
            .build();

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
        this.edgeHttpTest = new EdgeHttpTest(webTestClient);
    }

    @Test
    public void findMinTimePath() throws Exception {
        StationResponseView station1 = stationHttpTest.create(STATION_NAME);
        StationResponseView station2 = stationHttpTest.create(STATION_NAME_2);
        StationResponseView station3 = stationHttpTest.create(STATION_NAME_2);
        stationHttpTest.create(STATION_NAME_3);
        LineResponseView line = lineHttpTest.create(lineRequestView);
        edgeHttpTest.createEdge(line.getId(), station1.getId(), station2.getId(), 10, 10);
        edgeHttpTest.createEdge(line.getId(), station2.getId(), station3.getId(), 10, 10);


        webTestClient.get().uri("/paths?startId=" + station1.getId() + "&endId=" + station3.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.startStationId").isEqualTo(station1.getId())
                .jsonPath("$.endStationId").isEqualTo(station3.getId())
                .jsonPath("$.stations.length()").isEqualTo(3);
    }
}
