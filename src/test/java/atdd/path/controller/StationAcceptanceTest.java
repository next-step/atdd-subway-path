package atdd.path.controller;

import atdd.AbstractAcceptanceTest;
import atdd.path.domain.dto.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class StationAcceptanceTest extends AbstractAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    private StationHttpTest stationHttpTest;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
    }

    @Test
    public void 지하철역_등록() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\"" + stationName + "\"}";

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(stationName);
    }

    @Test
    public void 지하철역_목록_조회() {
        String stationName = "강남역";
        stationHttpTest.createStationTest(stationName).getResponseBody();

        webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(StationDto.class)
                .hasSize(1);
    }

    @Test
    public void 지하철역_정보_조회() {
        String stationName = "강남역";
        Long stationId = stationHttpTest.createStationTest(stationName).getResponseBody().getId();

        webTestClient.get().uri("/stations/{stationId}", stationId)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("$.name").isEqualTo(stationName);
    }

    @Test
    public void 지하철역_삭제() {
        String stationName = "강남역";
        Long stationId = stationHttpTest.createStationTest(stationName).getResponseBody().getId();

        webTestClient.delete().uri("/stations/{stationId}", stationId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
