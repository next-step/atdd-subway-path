package atdd.station;

import atdd.station.domain.StationRepository;
import atdd.station.web.dto.StationResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.util.Arrays;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private StationRepository stationRepository;

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

    @DisplayName("지하철역 목록을 조회한다")
    @Test
    public void retrieveStations() {
        // given
        createStation("강남역");
        createStation("잠실역");

        // when, then
        webTestClient.get()
                .uri("/stations")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(StationResponseDto.class)
                .hasSize(2)
                .isEqualTo(Arrays.asList(StationResponseDto.of("강남역"), StationResponseDto.of("잠실역")));
    }

    @DisplayName("지하철역 조회한다")
    @ParameterizedTest
    @ValueSource(strings = {"강남역", "잠실역", "장한평역"})
    public void retrieveFromStationName(String stationName) {
        // given
        EntityExchangeResult<Void> createdResult = createStation(stationName);

        // when, then
        getFromStationName(stationName, createdResult);
    }

    private void getFromStationName(String stationName,
                                    EntityExchangeResult<Void> createdResult) {

        webTestClient.get()
                .uri(createdResult.getResponseHeaders().getLocation().getPath())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StationResponseDto.class)
                .isEqualTo(StationResponseDto.of(stationName));
    }

    @DisplayName("지하철역 삭제한다")
    @Test
    public void deleteFromStationName() {
        // given
        String stationName = "강남역";
        EntityExchangeResult<Void> createdResult = createStation(stationName);

        // when, then
        webTestClient.delete()
                .uri(createdResult.getResponseHeaders().getLocation().getPath())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @AfterEach
    public void tearDown() {
        stationRepository.deleteAll();
    }

}
