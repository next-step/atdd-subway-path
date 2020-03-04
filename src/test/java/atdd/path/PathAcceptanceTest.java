package atdd.path;

import atdd.BaseAcceptanceTest;
import atdd.path.api.response.PathResponseView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import static atdd.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends BaseAcceptanceTest {

    public static final String PATH_DISTANCE_URL = "/paths/distance";

    @DisplayName("최단 거리 경로를 조회 할 수 있다")
    @Test
    void beAbleFindShortestDistancePath() {
        createStation(TEST_STATION);
        createStation(TEST_STATION_2);
        createStation(TEST_STATION_3);
        createStation(TEST_STATION_4);
        createStation(TEST_STATION_5);
        createStation(TEST_STATION_6);
        createStation(TEST_STATION_7);
        createStation(TEST_STATION_8);
        createStation(TEST_STATION_9);
        createStation(TEST_STATION_10);
        createStation(TEST_STATION_11);
        createStation(TEST_STATION_12);

        createLine(TEST_LINE);
        createLine(TEST_LINE_2);

        createEdge(LINE_ID, TEST_EDGE_23);
        createEdge(LINE_ID, TEST_EDGE);
        createEdge(LINE_ID, TEST_EDGE_2);
        createEdge(LINE_ID, TEST_EDGE_3);
        createEdge(LINE_ID, TEST_EDGE_4);
        createEdge(LINE_ID_2, TEST_EDGE_5);
        createEdge(LINE_ID_2, TEST_EDGE_6);
        createEdge(LINE_ID_2, TEST_EDGE_7);

        EntityExchangeResult<PathResponseView> result = webTestClient.get().uri(uriBuilder ->
                uriBuilder.path(PATH_DISTANCE_URL)
                        .queryParam("startId", STATION_ID_3)
                        .queryParam("endId", STATION_ID_6)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PathResponseView.class)
                .returnResult();

        PathResponseView view = result.getResponseBody();

        assertThat(view).isNotNull();
        assertThat(view.getStartStationId()).isEqualTo(STATION_ID_3);
        assertThat(view.getEndStationId()).isEqualTo(STATION_ID_6);
        assertThat(view.getStations().size()).isEqualTo(4);
    }

}
