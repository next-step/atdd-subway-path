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
    public static final String PATH_TIME_URL = "/paths/time";

    @DisplayName("최단 거리 경로를 조회 할 수 있다")
    @Test
    void beAbleFindShortestDistancePath() {
        createPath();

        PathResponseView view = getPathResponseView(PATH_DISTANCE_URL, STATION_ID_3, STATION_ID_6);

        assertThat(view).isNotNull();
        assertThat(view.getStartStationId()).isEqualTo(STATION_ID_3);
        assertThat(view.getEndStationId()).isEqualTo(STATION_ID_6);
        assertThat(view.getStations().size()).isEqualTo(4);
    }

    @DisplayName("최단 시간 경로를 조회 할 수 있다")
    @Test
    void beAbleFindShortestTimePath() {
        createPath();

        PathResponseView view = getPathResponseView(PATH_TIME_URL, STATION_ID_13, STATION_ID);

        assertThat(view).isNotNull();
        assertThat(view.getStartStationId()).isEqualTo(STATION_ID_13);
        assertThat(view.getEndStationId()).isEqualTo(STATION_ID);
        assertThat(view.getStations().size()).isEqualTo(3);
        assertThat(view.getStations()).extracting("name").contains(STATION_NAME_6);
    }

    private void createPath() {
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
        createStation(TEST_STATION_13);

        createLine(TEST_LINE);
        createEdge(LINE_ID, TEST_EDGE_23);
        createEdge(LINE_ID, TEST_EDGE);
        createEdge(LINE_ID, TEST_EDGE_2);
        createEdge(LINE_ID, TEST_EDGE_3);
        createEdge(LINE_ID, TEST_EDGE_4);

        createLine(TEST_LINE_2);
        createEdge(LINE_ID_2, TEST_EDGE_5);
        createEdge(LINE_ID_2, TEST_EDGE_6);
        createEdge(LINE_ID_2, TEST_EDGE_7);

        createLine(TEST_LINE_3);
        createEdge(LINE_ID_3, TEST_EDGE_10);
        createEdge(LINE_ID_3, TEST_EDGE_11);
        createEdge(LINE_ID_3, TEST_EDGE_12);
    }

    private PathResponseView getPathResponseView(String url, Long startId, Long endId) {
        EntityExchangeResult<PathResponseView> result = webTestClient.get().uri(uriBuilder ->
                uriBuilder.path(url)
                        .queryParam("startId", startId)
                        .queryParam("endId", endId)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PathResponseView.class)
                .returnResult();

        return result.getResponseBody();
    }

}
