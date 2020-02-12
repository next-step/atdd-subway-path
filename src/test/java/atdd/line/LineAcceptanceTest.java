package atdd.line;

import atdd.BaseAcceptanceTest;
import atdd.line.api.response.LineResponseView;
import atdd.station.api.response.StationResponseView;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static atdd.util.TestUtils.getLocationPath;
import static atdd.util.TestUtils.jsonOf;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class LineAcceptanceTest extends BaseAcceptanceTest {

    private static final Logger logger = LoggerFactory.getLogger(LineAcceptanceTest.class);

    private String lineName;
    private String inputJson;

    @BeforeAll
    void setUp() {
        lineName = "2호선";
        inputJson = jsonOf(getLineData(lineName));
    }

    @DisplayName("지하철 노선 등록을 할 수 있다")
    @Test
    void beAbleCreateLine() {
        EntityExchangeResult<LineResponseView> result = createLine(inputJson);
        LineResponseView line = result.getResponseBody();

        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo(lineName);
    }

    @DisplayName("지하철 노선 목록 조회를 할 수 있다")
    @Test
    void beAbleFindLines() {
        createLine(inputJson);

        webTestClient.get().uri("/lines")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(1)
                .jsonPath("$.lines[0].name").isEqualTo(lineName);
    }

    @DisplayName("지하철 노선 정보를 조회 할 수 있다")
    @Test
    void beAbleFindLineById() {
        EntityExchangeResult<LineResponseView> result = createLine(inputJson);
        String path = getLocationPath(result.getResponseHeaders());

        webTestClient.get().uri(path)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(lineName);
    }

    @DisplayName("지하철 노선을 삭제 할 수 있다")
    @Test
    void beAbleDeleteStation() {
        EntityExchangeResult<LineResponseView> result = createLine(inputJson);
        String path = getLocationPath(result.getResponseHeaders());

        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNoContent();
    }

    @TestInstance(PER_CLASS)
    @DisplayName("지하철 노선 구간 테스트")
    @Nested
    class EdgeTest {

        private Map<String, Long> stationData;
        private LineResponseView lineData;

        @BeforeAll
        void setUp() {
            deleteAll();

            stationData = createStations("강남역", "역삼역", "선릉역");

            EntityExchangeResult<LineResponseView> line = createLine(inputJson);
            lineData = line.getResponseBody();
        }

        @DisplayName("지하철 노선에 지하철 구간을 등록 할 수 있다")
        @Test
        void beAbleCreateEdge() {
            Long sourceStationId = stationData.get("강남역");
            Long targetStationId = stationData.get("역삼역");

            Map<String, Object> edgeData = getEdgeData(sourceStationId, targetStationId);
            String inputJson = jsonOf(edgeData);

            webTestClient.post().uri("/lines/" + lineData.getId() + "/edges")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(inputJson), String.class)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectHeader().exists("Location")
                    .expectBody()
                    .jsonPath("$.stations.length()").isEqualTo(2)
                    .jsonPath("$.stations[0].id").isEqualTo(targetStationId)
                    .jsonPath("$.stations[1].id").isEqualTo(sourceStationId);

            webTestClient.get().uri("/stations/" + sourceStationId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.lines.length()").isEqualTo(1)
                    .jsonPath("$.lines[0].name").isEqualTo(lineData.getName());
        }
    }

    private Map<String, Object> getLineData(String lineName) {
        return Map.ofEntries(
                Map.entry("name", lineName),
                Map.entry("startTime", "05:30"),
                Map.entry("endTime", "00:30"),
                Map.entry("intervalTime", 5)
        );
    }

    private Map<String, Object> getEdgeData(Long sourceStationId, Long targetStationId) {
        return Map.ofEntries(
                Map.entry("elapsedTime", 2),
                Map.entry("distance", 1.2),
                Map.entry("sourceStationId", sourceStationId),
                Map.entry("targetStationId", targetStationId)
        );
    }

    private Map<String, Long> createStations(String... stationNames) {
        return Arrays.stream(stationNames)
                .map(stationName -> jsonOf(Map.of("name", stationName)))
                .map(this::createStation)
                .map(EntityExchangeResult::getResponseBody)
                .filter(Objects::nonNull)
                .collect(toMap(StationResponseView::getName, StationResponseView::getId));
    }

}
