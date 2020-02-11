package atdd.station.controller;

import atdd.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    public static final String KANGNAM_STATION_JSON = "{\"name\": \"" + KANGNAM_STATION_NAME + "\"}";
    private static final String NAME_JSON_PARSE_EXPRESSION = "$.name";
    private static final String ID_JSON_PARSE_EXPRESSION = "$.id";
    private static final String STATION_API_BASE_URL = "/stations/";
    private static final String LOCATION_HEADER = "Location";

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("강남역_지하철_등록을_요청이_성공하는지")
    @ParameterizedTest
    @ValueSource(strings = {KANGNAM_STATION_NAME, PANGYO_STATION_NAME})
    void createStationSuccessTest(String stationName) {
        //when
        //then
        webTestClient.post().uri(STATION_API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(getStation(stationName)), Station.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(LOCATION_HEADER)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(stationName)
                .jsonPath(ID_JSON_PARSE_EXPRESSION).isEqualTo("1")
                .jsonPath("$.subwayLines[0].name").isEqualTo("2호선");
    }

    @DisplayName("강남역_지하철이_조회가_성공하는지")
    @Test
    void listStationSuccessTest() {
        //given
        creatStation(KANGNAM_STATION_NAME);
        creatStation(PANGYO_STATION_NAME);

        //when
        //then
        webTestClient.get().uri(STATION_API_BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath(getStationNameJsonParseExpressionByIndex("0")).isEqualTo(KANGNAM_STATION_NAME)
                .jsonPath(getStationNameJsonParseExpressionByIndex("1")).isEqualTo(PANGYO_STATION_NAME);
    }


    @DisplayName("강남역_지하철_역_정보_상세조회_요청이_성공하는지")
    @Test
    void stationDetailSuccessTest() {
        //given
        String location = creatStation(KANGNAM_STATION_NAME);

        //when
        //then
        webTestClient.get().uri(location)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(KANGNAM_STATION_NAME);
    }


    @DisplayName("강남역_지하철_역_정보_삭제_요청이_성공하는지")
    @Test
    void stationDeleteSuccessTest() {
        String location = creatStation(KANGNAM_STATION_NAME);

        //when
        webTestClient.delete().uri(location)
                .exchange()
                .expectStatus().isOk();
    }

    private String creatStation(String name) {
        return Objects.requireNonNull(webTestClient.post().uri(STATION_API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(getStation(name)), Station.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(LOCATION_HEADER)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(name)
                .returnResult()
                .getResponseHeaders()
                .getLocation()).getPath();
    }

    private String getStationNameJsonParseExpressionByIndex(String index) {
        return "$.stations[" + index + "].name";
    }
}
