package atdd.path;

import atdd.BaseAcceptanceTest;
import atdd.domain.Station;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.net.URI;

public class StationAcceptanceTest extends BaseAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Test
    void createStation() {
        //given
        Station station = Station.builder()
                .name("강남")
                .build();

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(station)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.name").isEqualTo("강남");
    }
}
