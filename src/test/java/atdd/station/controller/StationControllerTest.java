package atdd.station.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class StationControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void create() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\""+stationName+"\"}";

        client.post().uri("/stations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(inputJson), String.class)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().exists("Location")
            .expectBody().jsonPath("$.name").isEqualTo(stationName);
    }

    @Test
    void get() {
        client.get().uri("/stations/1")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody().jsonPath("$.name").isEqualTo("강남역");
    }

    @Test
    void getAll() {
        client.get().uri("/stations")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody().jsonPath("$.[0].name").isEqualTo("강남역");
    }

    @Test
    void delete() {
        client.delete().uri("/stations/1")
            .exchange()
            .expectStatus().isNoContent();
    }
}