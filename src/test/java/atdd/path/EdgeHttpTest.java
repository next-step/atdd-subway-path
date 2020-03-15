package atdd.path;

import atdd.path.dto.EdgeRequestView;
import atdd.path.dto.EdgeResponseView;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.stream.Collectors;

public class EdgeHttpTest {
    private WebTestClient webTestClient;

    public EdgeHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public EdgeResponseView create( Long lineId, Long sourceId, Long targetId, int distance){
        EdgeRequestView requestView
                = EdgeRequestView.builder()
                .lineId(lineId)
                .sourceId(sourceId)
                .targetId(targetId)
                .distance(distance)
                .build();

        return webTestClient.post().uri("/edges")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestView)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(EdgeResponseView.class)
                .getResponseBody()
                .toStream()
                .collect(Collectors.toList())
                .get(0);
    }
}
