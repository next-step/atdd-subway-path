package atdd.line;

import atdd.BaseAcceptanceTest;
import atdd.line.api.response.LineResponseView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import static atdd.TestConstant.*;
import static atdd.line.controller.LineController.EDGE_URL;
import static atdd.line.controller.LineController.LINE_URL;
import static atdd.station.controller.StationController.STATION_URL;
import static atdd.util.TestUtils.getLocationPath;
import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends BaseAcceptanceTest {

    @DisplayName("지하철 노선 등록을 할 수 있다")
    @Test
    void beAbleCreateLine() {
        EntityExchangeResult<LineResponseView> result = createLine(TEST_LINE);
        LineResponseView line = result.getResponseBody();

        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo(LINE_NAME);
    }

    @DisplayName("지하철 노선 목록 조회를 할 수 있다")
    @Test
    void beAbleFindLines() {
        createLine(TEST_LINE);

        webTestClient.get().uri(LINE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(1)
                .jsonPath("$.lines[0].name").isEqualTo(LINE_NAME);
    }

    @DisplayName("지하철 노선 정보를 조회 할 수 있다")
    @Test
    void beAbleFindLineById() {
        EntityExchangeResult<LineResponseView> result = createLine(TEST_LINE);
        String path = getLocationPath(result.getResponseHeaders());

        webTestClient.get().uri(path)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(LINE_NAME);
    }

    @DisplayName("지하철 노선을 삭제 할 수 있다")
    @Test
    void beAbleDeleteStation() {
        EntityExchangeResult<LineResponseView> result = createLine(TEST_LINE);
        String path = getLocationPath(result.getResponseHeaders());

        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNoContent();
    }

    @DisplayName("지하철 노선에 지하철 구간을 등록 할 수 있다")
    @Test
    void beAbleCreateEdge() {
        createStation(TEST_STATION);
        createStation(TEST_STATION_2);
        createStation(TEST_STATION_3);
        createLine(TEST_LINE);

        EntityExchangeResult<LineResponseView> result = createEdge(LINE_ID, TEST_EDGE);
        LineResponseView view = result.getResponseBody();

        assertThat(view).isNotNull();
        assertThat(view.getStations()).isNotNull();
        assertThat(view.getStations().size()).isEqualTo(2);
        assertThat(view.getStations()).extracting("id")
                .contains(STATION_ID, STATION_ID_2);

        webTestClient.get().uri(STATION_URL + "/" + STATION_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.lines.length()").isEqualTo(1)
                .jsonPath("$.lines[0].name").isEqualTo(LINE_NAME);
    }

    @DisplayName("지하철 노선에 지하철 구간을 삭제 할 수 있다")
    @Test
    void beAbleDeleteEdge() {
        createStation(TEST_STATION);
        createStation(TEST_STATION_2);
        createStation(TEST_STATION_3);

        createLine(TEST_LINE);

        createEdge(LINE_ID, TEST_EDGE);
        createEdge(LINE_ID, TEST_EDGE_2);

        webTestClient.delete().uri(uriBuilder ->
                uriBuilder.path(LINE_URL + "/" + LINE_ID + EDGE_URL)
                        .queryParam("stationId", STATION_ID_2)
                        .build())
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri("/lines/" + LINE_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.stations.length()").isEqualTo(2)
                .jsonPath("$.stations[0].id").isEqualTo(STATION_ID_3)
                .jsonPath("$.stations[1].id").isEqualTo(STATION_ID);
    }

}
