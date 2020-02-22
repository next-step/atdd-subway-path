package atdd.station;

import atdd.edge.Edge;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import reactor.core.publisher.Mono;

public class EdgeAcceptanceTest extends AbstractWebTestClientTest {

    private EntityExchangeResult<Edge> createEdge() {

        final String inputJson = "{\"line\":1,\"elapsedTime\":5,\"distance\":2.0,\"sourceStation\":1,\"targetStation\":2,}";

        return webTestClient.post().uri("/edges")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Edge.class)
                .returnResult();
    }

    @Test
    void create() {
        //expect
        createEdge();
    }

    @Test
    void delete() {

        final String path = createEdge().getResponseHeaders().getLocation().getPath();

        //expect
        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void show() {
        final String path = createEdge().getResponseHeaders().getLocation().getPath();
        //expect
        webTestClient.get().uri(path)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Edge.class);
    }


}
