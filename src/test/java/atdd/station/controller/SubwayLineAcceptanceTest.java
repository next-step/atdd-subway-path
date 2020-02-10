package atdd.station.controller;

import atdd.station.domain.SubwayLine;
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

import java.util.Objects;

import static atdd.station.fixture.StationFixture.KANGNAM_STATION_NAME;
import static atdd.station.fixture.SubwayLineFixture.SECOND_SUBWAY_LINE;
import static atdd.station.fixture.SubwayLineFixture.getSubwayLine;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class SubwayLineAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(SubwayLineAcceptanceTest.class);

    private static final String NAME_JSON_PARSE_EXPRESSION = "$.name";
    private static final String SUBWAY_LINE_API_BASE_URL = "/subway-lines/";
    private static final String LOCATION_HEADER = "Location";

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("2호선_지하철노선_생성이_성공하는지")
    @Test
    void createSubwayLineSuccessTest() {
        //given
        SubwayLine subwayLine = getSubwayLine(SECOND_SUBWAY_LINE);

        //when
        //then
        webTestClient.post().uri(SUBWAY_LINE_API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(subwayLine), SubwayLine.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(LOCATION_HEADER)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo("2호선")
                .jsonPath("$.startTime").isEqualTo("05:00")
                .jsonPath("$.endTime").isEqualTo("23:50")
                .jsonPath("$.intervalTime").isEqualTo("10")
                .jsonPath(getStationNameJsonParseExpressionByIndex("0")).isEqualTo(KANGNAM_STATION_NAME);
    }

    @DisplayName("지하철노선_조회가_성공하는지")
    @Test
    void listSubwayLineSuccessTest() {
        creatSubwayLine("2호선");
        creatSubwayLine("1호선");

        //when
        //then
        webTestClient.get().uri(SUBWAY_LINE_API_BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(SubwayLine.class).hasSize(2);
    }

    @DisplayName("지하철노선_상세조회가_성공하는지")
    @Test
    void detailSubwayLineSuccessTest() {
        String location = creatSubwayLine("2호선");

        //when
        //then
        webTestClient.get().uri(location)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo("2호선")
                .jsonPath("$.startTime").isEqualTo("05:00")
                .jsonPath("$.endTime").isEqualTo("23:50")
                .jsonPath("$.interval").isEqualTo("10")
                .jsonPath(getStationNameJsonParseExpressionByIndex("0")).isEqualTo("교대역");
    }

    @DisplayName("지하철노선_삭제가_성공하는지")
    @Test
    void deleteSubwayLineSuccessTest() {
        String location = creatSubwayLine("2호선");

        //when
        //then
        webTestClient.delete().uri(location)
                .exchange()
                .expectStatus().isOk();
    }


    private String creatSubwayLine(String subwayLineName) {
        return Objects.requireNonNull(webTestClient.post().uri(SUBWAY_LINE_API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just("{\"name\": \"" + subwayLineName + "\"}"), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(LOCATION_HEADER)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(subwayLineName)
                .returnResult()
                .getResponseHeaders()
                .getLocation()).getPath();
    }


    private String getStationNameJsonParseExpressionByIndex(String index) {
        return "$.stations[" + index + "].name";
    }
}
