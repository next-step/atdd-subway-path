package atdd.station.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Mono;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class StationControllerTest {

    private String STATION_NAME_GANGNAM = "강남역";
    private String STATION_NAME_YEOKSAM = "역삼역";

    @Autowired
    private WebTestClient client;

    @Test
    @DisplayName("지하철역 등록")
    void create() {
        create(STATION_NAME_GANGNAM).expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectHeader().exists("Location")
        .expectBody().jsonPath("$.name").isEqualTo(STATION_NAME_GANGNAM);
    }

    @Test
    @DisplayName("지하철역 조회")
    void get() {
        //given
        create(STATION_NAME_GANGNAM);
        //when & than
        client.get().uri("/stations/1")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody().jsonPath("$.name").isEqualTo(STATION_NAME_GANGNAM);
    }

    @Test
    @DisplayName("지하철역 목록 조회")
    void getAll() {
        //given
        create(STATION_NAME_GANGNAM);
        create(STATION_NAME_YEOKSAM);
        //when & than
        client.get().uri("/stations")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody().jsonPath("$.[0].name").isEqualTo(STATION_NAME_GANGNAM)
            .jsonPath("$.[1].name").isEqualTo(STATION_NAME_YEOKSAM);
    }

    @Test
    void delete() {
        client.delete().uri("/stations/1")
            .exchange()
            .expectStatus().isNoContent();
    }

    private ResponseSpec create (String name) {
        String inputJson = "{\"name\":\""+name+"\"}";
        return client.post().uri("/stations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(inputJson), String.class)
            .exchange();

    }
}