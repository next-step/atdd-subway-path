package atdd.station;

import atdd.station.domain.dto.StationDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

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
        StationDto stationDto = this.createStationTest(stationName);

        webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(StationDto.class)
                .hasSize(1);
    }

    @Test
    public void 지하철역_정보_조회() {
        String stationName = "강남역";
        Long stationId = this.createStationTest(stationName).getId();

        webTestClient.get().uri("/stations/{stationId}", stationId)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("$.name").isEqualTo(stationName);
    }

    @Test
    public void 지하철역_삭제() {
        String stationName = "강남역";
        Long stationId = this.createStationTest(stationName).getId();

        webTestClient.delete().uri("/stations/{stationId}", stationId)
                .exchange()
                .expectStatus().isNoContent();
    }

    private StationDto createStationTest(String stationName) {
        String inputJson = "{\"name\":\"" + stationName + "\"}";

        return webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectBody(StationDto.class)
                .returnResult()
                .getResponseBody();
    }
}
