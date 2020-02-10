package atdd.line;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalTime;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LineAcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void 지하철_노선_등록() {
        String line = "2호선";
        String input = "{" +
                "\"name\": \"" + line + "\"," +
                "\"startTime\": \"05:00\"," +
                "\"endTime\": \"23:50\"," +
                "\"intervalTime\": \"10\"" +
                "}";

        webTestClient.post().uri("/line")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(input), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(LineDto.class)
                .isEqualTo(LineDto.builder()
                        .name("2호선")
                        .startTime(LocalTime.of(5, 0))
                        .endTime(LocalTime.of(23, 50))
                        .intervalTime(10)
                        .build());
    }

    @Test
    public void 지하철_노선_목록_조회() {
        // when
        LineDto lineDto = this.createLineTest();

        webTestClient.get().uri("/line")
                .exchange()
                .expectBodyList(LineDto.class)
                .hasSize(1)
                .contains(lineDto);
    }

    private LineDto createLineTest() {
        String line = "2호선";
        String input = "{" +
                "\"name\": \"" + line + "\"," +
                "\"startTime\": \"05:00\"," +
                "\"endTime\": \"23:50\"," +
                "\"intervalTime\": \"10\"" +
                "}";

        return webTestClient.post().uri("/line")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(input), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(LineDto.class)
                .isEqualTo(LineDto.builder()
                        .name("2호선")
                        .startTime(LocalTime.of(5, 0))
                        .endTime(LocalTime.of(23, 50))
                        .intervalTime(10)
                        .build())
                .returnResult().getResponseBody();
    }
}
