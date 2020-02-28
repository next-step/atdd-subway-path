package atdd.path.controller;

import atdd.path.domain.dto.EdgeDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class EdgeHttpTest {
    private final WebTestClient webTestClient;

    public EdgeHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public EntityExchangeResult<EdgeDto> createEdge(Long lineId, Long sourceStationId, Long targetStationId) {
        String input = "{" +
                "\"lineId\": " + lineId + "," +
                "\"sourceStationId\": " + sourceStationId + "," +
                "\"targetStationId\": " + targetStationId +
                "}";

        return webTestClient.post().uri("/edges")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(input), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(EdgeDto.class)
                .returnResult();
    }
}
