package atdd.path.controller;

import atdd.AbstractAcceptanceTest;
import atdd.path.domain.dto.StationDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class StationHttpTest extends AbstractAcceptanceTest {
    private final WebTestClient webTestClient;

    public StationHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public EntityExchangeResult<StationDto> createStationTest(String stationName) {
        String inputJson = "{\"name\":\"" + stationName + "\"}";

        return webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectBody(StationDto.class)
                .returnResult();
    }

    public EntityExchangeResult<StationDto> findStationByIdTest(Long id) {
        return webTestClient.get().uri("/stations/{lineId}", id)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(StationDto.class)
                .returnResult();
    }
}
