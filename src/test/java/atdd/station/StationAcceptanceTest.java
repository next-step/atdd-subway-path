package atdd.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import java.util.List;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName(value = "Station Controller 를 테스트한다")
public class StationAcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Nested
    @DisplayName(value = "역을 생성하는 API")
    class CreateStation {

        @Nested
        @DisplayName(value = "역 이름이 주어진다면")
        class GivenStationName {

            final String stationName = "강남역";
            final String inputJson = "{\"name\":\"" + stationName + "\"}";

            @Test
            @DisplayName(value = "생성된 역을 리턴한다")
            void expectCreateStation() {
                webTestClient.post().uri("/stations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(inputJson), String.class)
                        .exchange()
                        .expectStatus().isCreated()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectHeader().exists("Location")
                        .expectBody(Station.class);
            }

        }

    }

    @Test
    public void getStations() {
        webTestClient.get().uri("/stations")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(List.class)
                .returnResult();
    }
}
