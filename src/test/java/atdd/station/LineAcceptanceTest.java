package atdd.station;

import atdd.line.LineDto;
import atdd.station.support.LineHttpSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class LineAcceptanceTest extends AbstractWebTestClientTest {

    @Test
    void create() {
        //expect
        LineHttpSupport.create(webTestClient);
    }

    @Test
    void delete() {

        final String path = LineHttpSupport.create(webTestClient).getResponseHeaders().getLocation().getPath();

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
        final String path = LineHttpSupport.create(webTestClient).getResponseHeaders().getLocation().getPath();
        //expect
        webTestClient.get().uri(path)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LineDto.Response.class);
    }
}
