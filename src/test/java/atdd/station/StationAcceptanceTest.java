package atdd.station;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@DirtiesContext()
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void 지하철역_등록() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\"" + stationName + "\"}";

        webTestClient.post().uri("/station")
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void 지하철역_목록_조회() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\"" + stationName + "\"}";
        webTestClient.post().uri("/station")
                .body(Mono.just(inputJson), String.class);

        webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .json("[{\"name\":\"" + stationName + "\"}]");
    }

    @Test
    public void 지하철역_정보_조회() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\"" + stationName + "\"}";
        webTestClient.post().uri("/station")
                .body(Mono.just(inputJson), String.class);

        webTestClient.get().uri("/station?name={stationName}", stationName)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .json("{\"name\":\"" + stationName + "\"}");
    }

    @Test
    public void 지하철역_삭제() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\"" + stationName + "\"}";
        webTestClient.post().uri("/station")
                .body(Mono.just(inputJson), String.class);

        webTestClient.delete().uri("/station?name={stationName}", stationName)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}
