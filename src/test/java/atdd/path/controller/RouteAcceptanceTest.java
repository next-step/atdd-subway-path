package atdd.path.controller;

import atdd.AbstractAcceptanceTest;
import atdd.path.domain.dto.LineDto;
import atdd.path.domain.dto.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RouteAcceptanceTest extends AbstractAcceptanceTest {

    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;
    private EdgeHttpTest edgeHttpTest;

    private StationDto gangnamStation;
    private StationDto yeoksamStation;
    private StationDto seonneungStation;
    private StationDto samsungStation;

    private LineDto secondLine;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
        this.edgeHttpTest = new EdgeHttpTest(webTestClient);

        // given
        this.gangnamStation = stationHttpTest.createStationTest("강남역").getResponseBody();
        this.yeoksamStation = stationHttpTest.createStationTest("역삼역").getResponseBody();
        this.seonneungStation = stationHttpTest.createStationTest("선릉역").getResponseBody();
        this.samsungStation = stationHttpTest.createStationTest("삼성역").getResponseBody();

        this.secondLine = lineHttpTest.createLineTest().getResponseBody();

        edgeHttpTest.createEdge(secondLine.getId(), gangnamStation.getId(), yeoksamStation.getId(), 2, 2);
        edgeHttpTest.createEdge(secondLine.getId(), yeoksamStation.getId(), seonneungStation.getId(), 3, 4);
        edgeHttpTest.createEdge(secondLine.getId(), seonneungStation.getId(), samsungStation.getId(), 2, 2);
    }

    @Test
    void 지하철역_사이의_최단_거리_경로_조회() {
        webTestClient.get().uri("routes/distance?startId=" + gangnamStation.getId() + "&endId=" + samsungStation.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("ETag")
                .expectBody()
                .jsonPath("$.startStationId").isEqualTo(gangnamStation.getId())
                .jsonPath("$.endStationId").isEqualTo(samsungStation.getId())
                .jsonPath("$.stations.size()").isEqualTo(4)
                .jsonPath("$.estimatedTime").isEqualTo(8);
    }

    @Test
    void 지하철역_사이의_최단_시간_경로_조회() {
        webTestClient.get().uri("routes/time?startId=" + gangnamStation.getId() + "&endId=" + samsungStation.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("ETag")
                .expectBody()
                .jsonPath("$.startStationId").isEqualTo(gangnamStation.getId())
                .jsonPath("$.endStationId").isEqualTo(samsungStation.getId())
                .jsonPath("$.stations.size()").isEqualTo(4)
                .jsonPath("$.estimatedTime").isEqualTo(8);
    }
}
