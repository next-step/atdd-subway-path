package atdd.station;

import com.sun.jndi.toolkit.url.Uri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("createStation")
    @Test
    void createStation() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\""+stationName+"\"}";

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @DisplayName("findStations")
    @Test
    void findStations() {
        String stationName = "강남역";

        webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(stationName)
        ;
    }

}
