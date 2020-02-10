package atdd.station.controller;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class StationAcceptanceTest
{
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    private final String STATIOIN_NAME = "강남역";
    private final String BASE_URL = "/stations";
    private final int TEST_ID = 1;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createStation()
    {
        String inputJson = "{\"name\":\""+ this.STATIOIN_NAME +"\"}";

        webTestClient.post().uri(BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(this.STATIOIN_NAME);
    }

    @Test
    public void list()
    {
        testCreateStation(STATIOIN_NAME);

        webTestClient.get().uri(BASE_URL + "/list")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.[0].name").isEqualTo(STATIOIN_NAME);
    }

    @Test
    public void detailById()
    {
        testCreateStation(STATIOIN_NAME);

        webTestClient.get().uri(BASE_URL + "/detail/" + TEST_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.name").isEqualTo(STATIOIN_NAME);

    }

    @Test
    public void deleteStation()
    {
        testCreateStation(STATIOIN_NAME);

        webTestClient.delete().uri(BASE_URL + "/delete/" + TEST_ID)
                .exchange()
                .expectStatus().isOk();
    }


    public void testCreateStation(String stationName)
    {
        String inputJson = "{\"name\":\""+ this.STATIOIN_NAME +"\"}";

        webTestClient.post().uri(BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(stationName);
    }
}
