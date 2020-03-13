package atdd.path;

import atdd.BaseAcceptanceTest;
import atdd.domain.Station;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

public class StationAcceptanceTest extends BaseAcceptanceTest {
    private final WebTestClient webTestClient;

    public StationAcceptanceTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    void createStation(){
        //given
        Station station = Station.builder()
                .name("강남")
                .build();

        webTestClient.post().uri(URI.create("/stations"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(station)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.name").isEqualTo("강남");
    }
}
