package atdd.station;

import atdd.BaseAcceptanceTest;
import atdd.station.api.response.StationResponseView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.Map;

import static atdd.util.TestUtils.getLocationPath;
import static atdd.util.TestUtils.jsonOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class StationAcceptanceTest extends BaseAcceptanceTest {

    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    private String stationName;
    private String inputJson;

    @BeforeAll
    void setUp() {
        stationName = "강남역";
        inputJson = jsonOf(Map.of("name", stationName));
    }

    @DisplayName("지하철역 등록을 할 수 있다")
    @Test
    void beAbleCreateStation() {
        EntityExchangeResult<StationResponseView> result = createStation(inputJson);
        StationResponseView station = result.getResponseBody();

        assertThat(station).isNotNull();
        assertThat(station.getName()).isEqualTo(stationName);
    }

    @DisplayName("지하철역 목록을 조회 할 수 있다")
    @Test
    void beAbleFindStations() {
        createStation(inputJson);

        webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.count").isEqualTo(1)
                .jsonPath("$.stations[0].name").isEqualTo(stationName);
    }

    @DisplayName("지하철역 정보를 조회 할 수 있다")
    @Test
    void beAbleFindStationsById() {
        EntityExchangeResult<StationResponseView> result = createStation(inputJson);
        String path = getLocationPath(result.getResponseHeaders());

        webTestClient.get().uri(path)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(stationName);
    }

    @DisplayName("지하철역 정보를 삭제 할 수 있다")
    @Test
    void beAbleDeleteStation() {
        EntityExchangeResult<StationResponseView> result = createStation(inputJson);
        String path = getLocationPath(result.getResponseHeaders());

        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNoContent();
    }

}
