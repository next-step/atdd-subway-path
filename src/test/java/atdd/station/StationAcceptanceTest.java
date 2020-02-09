package atdd.station;

import atdd.station.domain.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    StationRepository stationRepository;

    @DisplayName("지하철역을 등록한다")
    @ParameterizedTest
    @ValueSource(strings = {"강남역", "잠실역", "장한평역"})
    public void create(String stationName) {
        // expect
        createStation(stationName);
    }

    private EntityExchangeResult<Void> createStation(String stationName) {
        // given
        String inputJson = "{\"name\":\"" + stationName + "\"}";

        // when, then
        return webTestClient.post()
                .uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody(Void.class)
                .returnResult();
    }

    @AfterEach
    public void tearDown() {
        stationRepository.deleteAll();
    }
}
