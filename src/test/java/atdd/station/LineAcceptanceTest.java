package atdd.station;

import atdd.line.Line;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class LineAcceptanceTest extends AbstractWebTestClientTest {

    private

    @Test
    void create() {
        //given
        final String inputJson = "";

        //expect
        webTestClient.post().uri("/lines")
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
    void delete() {
        //expect
        webTestClient.delete().uri("/lines/1")
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.delete().uri("/lines/1")
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
