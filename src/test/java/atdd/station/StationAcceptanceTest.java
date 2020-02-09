package atdd.station;

import atdd.station.model.StationRequest;
import atdd.station.model.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final String GANGNAM_STATION_NAME = "강남역";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("지하철역 등록")
    public void createStation() {
        final StationRequest station = new StationRequest(GANGNAM_STATION_NAME);

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(station), StationRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(StationResponse.class)
                .value(StationResponse::getName, equalTo(GANGNAM_STATION_NAME));
    }

    @Test
    @DisplayName("지하철역 목록 조회")
    public void findAllStation() {
        final StationRequest station = new StationRequest(GANGNAM_STATION_NAME);

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(station), StationRequest.class)
                .exchange();

        final List<StationResponse> responses = webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(StationResponse.class)
                .hasSize(1)
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(responses.get(0).getName()).isEqualTo(GANGNAM_STATION_NAME);
    }

    @Test
    @DisplayName("지하철역 정보 조회")
    public void findStationByName() {
        final StationRequest station = new StationRequest(GANGNAM_STATION_NAME);

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(station), StationRequest.class)
                .exchange();

        webTestClient.get().uri("/stations/{stationName}", GANGNAM_STATION_NAME)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StationResponse.class)
                .value(StationResponse::getName, equalTo(GANGNAM_STATION_NAME));;
    }

    @Test
    @DisplayName("지하철역 삭제")
    public void deleteStation() {
        final StationRequest station = new StationRequest(GANGNAM_STATION_NAME);

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(station), StationRequest.class)
                .exchange();

        webTestClient.delete().uri("/stations?name={stationName}", GANGNAM_STATION_NAME)
                .exchange()
                .expectStatus().isOk();
    }
}
