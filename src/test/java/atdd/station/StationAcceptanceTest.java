package atdd.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관리")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("1개의 지하철역을 등록")
    @Test
    void createStation() {
        // given
        CreateStationRequest request = CreateStationRequest.builder()
            .name("강남역")
            .build();

        // when & then
        webTestClient.post().uri("/stations")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CreateStationRequest.class)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().exists("Location")
            .expectBody()
            .jsonPath("$.name").isEqualTo(request.getName());
    }

    @DisplayName("모든 지하철역 목록을 조회")
    @Test
    void findAllStations() {
        // given
        CreateStationRequest request = CreateStationRequest.builder()
            .name("강남역")
            .build();
        webTestClient.post().uri("/stations")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CreateStationRequest.class)
            .exchange();

        // when
        EntityExchangeResult<List<Station>> result = webTestClient.get().uri("/stations")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Station.class)
            .returnResult();

        // then
        assertThat(result.getResponseBody()).flatExtracting(Station::getName)
            .contains("강남역");
    }

    @DisplayName("특정 지하철역의 정보를 조회")
    @Test
    void findStationByName() {
        // given
        CreateStationRequest request = CreateStationRequest.builder()
            .name("강남역")
            .build();
        webTestClient.post().uri("/stations")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CreateStationRequest.class)
            .exchange();

        // when & then
        webTestClient.get().uri(uriBuilder -> uriBuilder
            .path("/station/")
            .queryParam("name", "강남역")
            .build())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(request.getName());
    }

    @DisplayName("특정 지하철역을 삭제")
    @Test
    void deleteStationByName() {
        // given
        CreateStationRequest request = CreateStationRequest.builder()
            .name("강남역")
            .build();
        webTestClient.post().uri("/stations")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), CreateStationRequest.class)
            .exchange();

        // when
        webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/station")
            .queryParam("name", "강남역")
            .build())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(request.getName());

        // then
        webTestClient.get().uri(uriBuilder -> uriBuilder
            .path("/station")
            .queryParam("name", "강남역")
            .build())
            .exchange()
            .expectStatus().isNotFound();
    }
}
