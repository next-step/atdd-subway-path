package atdd.station;

import atdd.station.web.dto.StationRequestDto;
import atdd.station.web.dto.StationResponseDto;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void test() {
        String stationName = "강남역";
        String inputJson = "{\"name\":\""+stationName+"\"}";

        webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(stationName);
    }

    @DisplayName("지하철역 등록 테스트")
    @Test
    public void createStationTest() {
        //given
        String name = "강남역";
        StationRequestDto stationRequestDto = StationRequestDto.builder().name(name).build();

        //when
        webTestClient.post().uri("createStation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(stationRequestDto),StationRequestDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(Long.class)
                .isEqualTo(1L);

        //then

    }

    @DisplayName("지하철역목록조회")
    @Test
    public void selectStationList() {

        webTestClient.get().uri("selectStationList")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.[0].name").isEqualTo("강남역")
                .jsonPath("$.[1].name").isEqualTo("수서역");
    }
}
