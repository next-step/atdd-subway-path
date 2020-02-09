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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);
    private static final String BASE_URI = "/stations";
    private static final String TARGET_STATION="강남역";
    private static final String inputJson = "{\"name\":\""+TARGET_STATION+"\"}";

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("station 등록이 제대로 되는가")
    @Test
    public void createStation() {
        //given

        //when, then
        webTestClient.post().uri(BASE_URI+"/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(TARGET_STATION);
    }

    @DisplayName("station 삭제가 제대로 되는가")
    @Test
    public void deleteStation(){
        //given
        createStation();

        //when, then
        webTestClient.delete().uri(BASE_URI+"/1")
                .exchange()
                .expectStatus().isOk();
    }

    @DisplayName("station 목록 조회가 제대로 되는가?")
    @Test
    public void getListStation(){
        //given
        createStation();
        createStation();

        //when, then
        webTestClient.get().uri(BASE_URI+"/list")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody().jsonPath("$.[0].name").isEqualTo(TARGET_STATION)
                .jsonPath("$.[1].name").isEqualTo(TARGET_STATION);
    }

    @DisplayName("station 조회가 제대로 되는가?")
    @Test
    public void detailStation(){
        //given
        createStation();

        //when, then
        webTestClient.get().uri(BASE_URI+"/detail/1")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo(TARGET_STATION);
    }
}