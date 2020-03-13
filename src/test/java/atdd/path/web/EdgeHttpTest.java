package atdd.path.web;

import atdd.path.application.dto.EdgeRequestViewFromClient;
import atdd.path.application.dto.EdgeResponseView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class EdgeHttpTest {
    public WebTestClient webTestClient;

    public EdgeHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    public EdgeResponseView createEdge(Long lineId, Long sourceId, Long targetId, int distance, int interval) {
        EdgeRequestViewFromClient edgeRequestViewFromClient
                = EdgeRequestViewFromClient.builder()
                .lineId(lineId)
                .sourceId(sourceId)
                .targetId(targetId)
                .distance(distance)
                .timeToTake(interval)
                .build();

        return webTestClient.post().uri("/edges/" + lineId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(edgeRequestViewFromClient)
                .exchange()
                .expectStatus().isOk()
                .returnResult(EdgeResponseView.class)
                .getResponseBody()
                .toStream()
                .collect(Collectors.toList())
                .get(0);
    }
}
