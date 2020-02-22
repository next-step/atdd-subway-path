package atdd.station;

import atdd.edge.Edge;
import atdd.edge.EdgeLink;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class EdgeHttpSupport {

    public static EntityExchangeResult<Edge> createEdge(WebTestClient webTestClient) {

        final String inputJson = "{\n" +
                "  \"lineId\": 1,\n" +
                "  \"elapsedTime\": 5,\n" +
                "  \"distance\": 2.0,\n" +
                "  \"sourceStationId\": 2,\n" +
                "  \"targetStationId\": 3\n" +
                "}";

        return webTestClient.post().uri(EdgeLink.ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Edge.class)
                .returnResult();
    }
}
