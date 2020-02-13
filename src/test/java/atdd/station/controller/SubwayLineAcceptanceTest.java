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

import static atdd.station.fixture.StationFixture.*;
import static atdd.station.fixture.SubwayLineFixture.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class SubwayLineAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(SubwayLineAcceptanceTest.class);

    private static final String NAME_JSON_PARSE_EXPRESSION = "$.name";
    private static final String SUBWAY_LINE_API_BASE_URL = "/subway-lines/";
    private static final String LOCATION_HEADER = "Location";
    private static final String STATION_JSON_PARSE_EXPRESSION = "$.startTime";
    private static final String END_TIME_JSON_PARSE_EXPRESSION = "$.endTime";
    private static final String INTERVAL_TIME_JSON_PARSE_EXPRESSION = "$.intervalTime";

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("2호선_지하철노선_생성이_성공하는지")
    @Test
    void createSubwayLineSuccessTest() {
        //when
        //then
        webTestClient.post().uri(SUBWAY_LINE_API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(getSubwayLine(SECOND_SUBWAY_LINE_NAME)), SubwayLine.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(LOCATION_HEADER)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo("2호선")
                .jsonPath(STATION_JSON_PARSE_EXPRESSION).isEqualTo("05:00")
                .jsonPath(END_TIME_JSON_PARSE_EXPRESSION).isEqualTo("23:50")
                .jsonPath(INTERVAL_TIME_JSON_PARSE_EXPRESSION).isEqualTo("10")
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
                .expectBodyList(SubwayLine.class).hasSize(1);
    }

    @DisplayName("지하철노선_상세조회가_성공하는지")
    @Test
    void detailSubwayLineSuccessTest() {
        String location = creatSubwayLine(SECOND_SUBWAY_LINE_NAME);

        //when
        //then
        webTestClient.get().uri(location)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(SECOND_SUBWAY_LINE_NAME)
                .jsonPath(STATION_JSON_PARSE_EXPRESSION).isEqualTo("05:00")
                .jsonPath(END_TIME_JSON_PARSE_EXPRESSION).isEqualTo("23:50")
                .jsonPath(INTERVAL_TIME_JSON_PARSE_EXPRESSION).isEqualTo("10");
    }

    @DisplayName("지하철노선_삭제가_성공하는지")
    @Test
    void deleteSubwayLineSuccessTest() {
        String location = creatSubwayLine(SECOND_SUBWAY_LINE_NAME);

        //when
        //then
        webTestClient.delete().uri(location)
                .exchange()
                .expectStatus().isOk();
    }

    @DisplayName("2호선_지하철노선에_강남역과_역삼역을_추가가_성공하는지")
    @Test
    void updateSecondSubwayToAddKanNamStationSuccessTest() {
        String location = creatSubwayLine(SECOND_SUBWAY_LINE_NAME);

        //whens
        //then
        webTestClient.put().uri(location)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(KANGNAM_AND_YUCKSAM_STATIONS), SubwayLine.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(SECOND_SUBWAY_LINE_NAME);
    }

    @DisplayName("2호선_지하철노선에_내에_존재하는_강남역을_삭제가_성공하는지")
    @Test
    void deleteKanNamStationInSecondSubwaySuccessTest() {
        String location = creatSecondSubwayLine();

        //whens
        //then
        webTestClient.delete().uri(location + "/" + YUCKSAM_STATION_NAME)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }


    private String creatSubwayLine(String subwayLineName) {
        return Objects.requireNonNull(webTestClient.post().uri(SUBWAY_LINE_API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(getSubwayLine(subwayLineName)), SubwayLine.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(LOCATION_HEADER)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(subwayLineName)
                .returnResult()
                .getResponseHeaders()
                .getLocation()).getPath();
    }

    private String creatSecondSubwayLine() {
        return Objects.requireNonNull(webTestClient.post().uri(SUBWAY_LINE_API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(getSecondSubwayLineName()), SubwayLine.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(LOCATION_HEADER)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(SECOND_SUBWAY_LINE_NAME)
                .returnResult()
                .getResponseHeaders()
                .getLocation()).getPath();
    }



    private String getStationNameJsonParseExpressionByIndex(String index) {
        return "$.stations[" + index + "].name";
    }
}
