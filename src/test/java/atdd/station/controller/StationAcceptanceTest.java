package atdd.station.controller;

import org.junit.jupiter.api.DisplayName;
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
    private final String BASE_URL = "/stations/";
    private final int TEST_ID = 1;
    private final String INPUT_JSON = "{\"name\":\""+ STATIOIN_NAME +"\"}";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("지하철역 등록")
    public void createStation()
    {
        createStation(STATIOIN_NAME);
    }

    @Test
    @DisplayName("지하철역 목록 조회")
    public void findStations()
    {
        createStation(STATIOIN_NAME);

        webTestClient.get().uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.[0].name").isEqualTo(STATIOIN_NAME);
    }

    @Test
    @DisplayName("지하철역 정보 조회")
    public void detailById()
    {
        createStation(STATIOIN_NAME);

        webTestClient.get().uri(BASE_URL + "/" + TEST_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.name").isEqualTo(STATIOIN_NAME);
    }

    @Test
    @DisplayName("지하철역 삭제")
    public void deleteStation()
    {
        createStation(STATIOIN_NAME);

        webTestClient.delete().uri(BASE_URL + "/" + TEST_ID)
                .exchange()
                .expectStatus().isOk();
    }

    public void createStation(String stationName)
    {
        webTestClient.post().uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(INPUT_JSON), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(stationName);
    }
}
