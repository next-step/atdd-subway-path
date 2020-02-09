package atdd.station.controller;

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

import static atdd.station.fixture.StationFixture.KANGNAM_STATION_NAME;
import static atdd.station.fixture.StationFixture.SINSA_STATION_NAME;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StationAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    public static final String KANGNAM_STATION_JSON = "{\"name\": \"" + KANGNAM_STATION_NAME + "\"}";
    private static final String NAME_JSON_PARSE_EXPRESSION = "$.name";
    private static final String ID_JSON_PARSE_EXPRESSION = "$.id";
    private static final String STATION_API_BASE_URL = "/stations";
    private static final String LOCATION_HEADER = "Location";

    @Autowired
    private WebTestClient webTestClient;

    @ParameterizedTest
    @ValueSource(strings = {KANGNAM_STATION_NAME, SINSA_STATION_NAME})
    void 강남역_지하철_등록을_요청이_성공하는지(String stationName) {
        //when
        //then
        webTestClient.post().uri(STATION_API_BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just("{\"name\": \"" + stationName + "\"}"), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists(LOCATION_HEADER)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(stationName)
                .jsonPath(ID_JSON_PARSE_EXPRESSION).isEqualTo("1");
    }

    @ParameterizedTest
    @ValueSource(strings = {KANGNAM_STATION_NAME, SINSA_STATION_NAME})
    void 강남역_지하철이_조회가_성공하는지(String stationName) {
        //given
        creatStation(KANGNAM_STATION_NAME);
        creatStation(SINSA_STATION_NAME);

        //when
        //then
        webTestClient.get().uri(STATION_API_BASE_URL + "/list")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath(getStationNameJsonParseExpressionByIndex("0")).isEqualTo(stationName)
                .jsonPath(getStationNameJsonParseExpressionByIndex("1")).isEqualTo(stationName);
    }

    @ParameterizedTest
    @ValueSource(strings = {KANGNAM_STATION_NAME, SINSA_STATION_NAME})
    void 강남역_지하철_역_정보_상세조회_요청이_성공하는지(String stationName) {
        //given
        long id = 1;
        creatStation(stationName);

        //when
        //then
        webTestClient.get().uri(STATION_API_BASE_URL + "/detail/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(stationName);

    }

    @ParameterizedTest
    @ValueSource(strings = {KANGNAM_STATION_NAME, SINSA_STATION_NAME})
    void 강남역_지하철_역_정보_삭제_요청이_성공하는지(String stationName) {
        creatStation(stationName);

        //when
        webTestClient.delete().uri("/1")
                .exchange()
                .expectStatus().isOk();
    }

    private void creatStation(String name) {
        webTestClient.post().uri(STATION_API_BASE_URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just("{\"name\": \"" + name + "\"}"), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath(NAME_JSON_PARSE_EXPRESSION).isEqualTo(name);
    }

    private String getStationNameJsonParseExpressionByIndex(String index) {
        return "$.stations[" + index + "].name";
    }
}
