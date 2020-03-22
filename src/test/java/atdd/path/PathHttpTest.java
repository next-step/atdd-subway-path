package atdd.path;

import atdd.path.dto.EdgeResponseView;
import atdd.path.dto.PathRequestView;
import atdd.path.dto.PathResponseView;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.stream.Collectors;

public class PathHttpTest {
    private WebTestClient webTestClient;

    public PathHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public PathResponseView findPath(Long startId, Long endId){
        PathRequestView requestView = new PathRequestView(startId, endId);

        return webTestClient.get().uri("/paths?startId="+startId+"&endId="+endId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(PathResponseView.class)
                .getResponseBody()
                .toStream()
                .collect(Collectors.toList())
                .get(0);
    }
}
