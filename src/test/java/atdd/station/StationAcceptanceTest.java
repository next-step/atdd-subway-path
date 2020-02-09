package atdd.station;

import atdd.station.api.response.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {

    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("지하철역 등록을 할 수 있다")
    @Test
    void beAbleCreateStation() {
        // give
        String stationName = "강남역";

        // when
        EntityExchangeResult<StationResponse> result = createStation(stationName);
        StationResponse station = result.getResponseBody();

        // then
        assertThat(station).isNotNull();
        assertThat(station.getName()).isEqualTo(stationName);
    }

    @DisplayName("지하철역 목록을 조회 할 수 있다")
    @Test
    void beAbleFindStations() {
        // given
        String stationName = "강남역";
        createStation(stationName);

        // when & then
        webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.count").isEqualTo(1)
                .jsonPath("$.stations[0].name", stationName);
    }

    @DisplayName("지하철역 정보를 조회 할 수 있다")
    @Test
    void beAbleFindStationsById() {
        // given
        String stationName = "강남역";

        EntityExchangeResult<StationResponse> result = createStation(stationName);
        String path = getLocationPath(result.getResponseHeaders());

        // when & then
        webTestClient.get().uri(path)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.stations[0].name", stationName);
    }

    @DisplayName("지하철역 정보를 삭제 할 수 있다")
    @Test
    void beAbleDeleteStation() {
        // given
        String stationName = "강남역";

        EntityExchangeResult<StationResponse> result = createStation(stationName);
        String path = getLocationPath(result.getResponseHeaders());

        // when & then
        webTestClient.delete().uri(path)
                .exchange()
                .expectStatus().isNoContent();
    }

    private EntityExchangeResult<StationResponse> createStation(String stationName) {
        // given
        String inputJson = "{\"name\":\""+ stationName +"\"}";

        // when
        return webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(StationResponse.class)
                .returnResult();
    }

    private String getLocationPath(HttpHeaders responseHeaders) {
        URI location = responseHeaders.getLocation();
        return Objects.nonNull(location) ? location.getPath() : "noLocation";
    }

}
