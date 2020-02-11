package atdd.station;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("지하철역을 등록한다")
    @Test
    public void createStation(){
        String STATION_NAME = "강남역";

        Station station = Station.builder()
                .name(STATION_NAME)
                .build();

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(station), Station.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(STATION_NAME)
        ;
    }

    @DisplayName("지하철역 목록을 조회한다")
    @Test
    public void getStations(){
        String STATION_NAME = "강남역";

        Station station = Station.builder()
                .name(STATION_NAME)
                .build();

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(station), Station.class)
                .exchange();

        List<Station> stations = webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Station.class)
                .returnResult()
                .getResponseBody()

        ;

        Assertions.assertThat(stations.get(0).getName()).isEqualTo(STATION_NAME);

    }

    @DisplayName("지하철역 정보 조회를 한다")
    @Test
    public void getStationById(){
        String STATION_NAME = "강남역";

        Station station = Station.builder()
                .name(STATION_NAME)
                .build();

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(station), Station.class)
                .exchange();


        webTestClient.get().uri("/stations/" + 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.name").isEqualTo(STATION_NAME)
        ;
    }

    @DisplayName("지하철역을 삭제한다")
    @Test
    public void deleteById(){
        String STATION_NAME = "강남역";

        Station station = Station.builder()
                .name(STATION_NAME)
                .build();

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(station), Station.class)
                .exchange();

        webTestClient.delete().uri("/stations/"+1)
                .exchange()
                .expectStatus().isNoContent();

    }
}
