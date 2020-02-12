package atdd;


import atdd.domain.stations.Stations;
import atdd.domain.stations.StationsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);
    private static final String BASE_URI = "/stations/";
    private static final String TARGET_STATION = "강남역";
    private static final String TARGET_STATION_2 = "역삼역";

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("station 등록이 제대로 되는가")
    @Test
    public void createStation() {
        //given

        //when, then
        webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Stations.builder()
                        .name(TARGET_STATION)
                        .build()), Stations.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(TARGET_STATION);
    }

    @DisplayName("station 삭제가 제대로 되는가")
    @Test
    public void deleteStation() {
        //given
        String location=createTestStations(TARGET_STATION);

        //when, then
        webTestClient.delete().uri(location)
                .exchange()
                .expectStatus().isOk();
    }

    @DisplayName("station 목록 조회가 제대로 되는가?")
    @Test
    public void readStationList() {
        //given
        createTestStations(TARGET_STATION);
        createTestStations(TARGET_STATION_2);

        //when, then
        webTestClient.get().uri(BASE_URI)
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody().jsonPath("$.[0].name").isEqualTo(TARGET_STATION)
                .jsonPath("$.[1].name").isEqualTo(TARGET_STATION_2);
    }

    @DisplayName("station 조회가 제대로 되는가?")
    @Test
    public void readStation() {
        //given
        String location=createTestStations(TARGET_STATION);

        //when, then
        webTestClient.get().uri(location)
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo(TARGET_STATION);
    }

    public String createTestStations(String name) {
        return webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Stations.builder()
                        .name(name)
                        .build()), Stations.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(name)
                .returnResult()
                .getResponseHeaders()
                .getLocation().getPath();
    }
}