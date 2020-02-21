package atdd.station;

import atdd.line.Line;
import atdd.line.LineLink;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import reactor.core.publisher.Mono;

public class LineAcceptanceTest extends AbstractWebTestClientTest {

    private EntityExchangeResult<Line> createLine() {

        final String inputJson = "{\"name\":\"1호선\",\"startTime\":\"05:30\",\"endTime\":\"23:30\",\"intervalTime\":5}";

        return webTestClient.post().uri(LineLink.ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Line.class)
                .returnResult();
    }

    @Test
    void create() {
        //expect
        createLine();
    }

    @Test
    void delete() {

        final String path = createLine().getResponseHeaders().getLocation().getPath();

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
        //expect
        webTestClient.get().uri("/lines")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Line.class);


    }
}
