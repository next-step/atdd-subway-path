package atdd.station.controller;

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

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class SubwayLineAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(SubwayLineAcceptanceTest.class);

    private static final String NAME_JSON_PARSE_EXPRESSION = "$.name";
    private static final String STATION_API_BASE_URL = "/subway-lines/";
    private static final String LOCATION_HEADER = "Location";

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("2호선_지하철노선_생성이_성공하는지")
    @Test
    void createSubwayLineSuccessTest() {
        //when
        //then
        webTestClient.post().uri(STATION_API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just("{\"name\": \"2호선\"}"), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(LOCATION_HEADER)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo("2호선")
                .jsonPath("$.startTime").isEqualTo("05:00")
                .jsonPath("$.endTime").isEqualTo("23:50")
                .jsonPath("$.interval").isEqualTo("10")
                .jsonPath(getStationNameJsonParseExpressionByIndex("0")).isEqualTo("교대역");
    }

    private String getStationNameJsonParseExpressionByIndex(String index) {
        return "$.stations[" + index + "].name";
    }
}
