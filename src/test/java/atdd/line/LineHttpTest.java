package atdd.line;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class LineHttpTest {

    private final WebTestClient webTestClient;

    public LineHttpTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    public EntityExchangeResult<LineDto> createLineTest() {
        String line = "2호선";
        String input = "{" +
                "\"name\": \"" + line + "\"," +
                "\"startTime\": \"05:00\"," +
                "\"endTime\": \"23:50\"," +
                "\"intervalTime\": \"10\"" +
                "}";

        return webTestClient.post().uri("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(input), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(LineDto.class)
                .returnResult();
    }

    public EntityExchangeResult<LineDto> findLineByTest(Long id) {
        return webTestClient.get().uri("/lines/{id}", id)
                .exchange()
                .expectBody(LineDto.class)
                .returnResult();
    }
}
