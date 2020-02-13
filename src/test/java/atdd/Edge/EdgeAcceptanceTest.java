package atdd.Edge;

import atdd.line.Line;
import atdd.line.LineDetailResponse;
import atdd.line.LineResponse;
import atdd.station.Station;
import atdd.station.StationAcceptanceTest;
import atdd.station.StationDetailResponse;
import atdd.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
class EdgeAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;
    StationResponse resultStationA;
    StationResponse resultStationB;
    StationResponse resultStationC;
    LineResponse resultLine;


    @BeforeEach
    void setUp(){
        resultStationA = createStation("강남역");
        resultStationB = createStation("역삼역");
        resultStationC = createStation("선릉역");
        resultLine = createLine("2호선");

    }

    @DisplayName("중복되는 create 테스트 코드")
    private LineResponse createLine(String stationName){
        LocalTime START_TIME = LocalTime.of(5,0);
        LocalTime END_TIME = LocalTime.of(23, 30);
        int INTERVAL_TIME = 10;
        int EXTRA_FARE = 0;

        Line line = Line.builder()
                .name(stationName)
                .start_time(START_TIME)
                .end_time(END_TIME)
                .interval_time(INTERVAL_TIME)
                .extra_fare(EXTRA_FARE)
                .build();
        LineResponse result = webTestClient.post().uri("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(line), Line.class)
                .exchange()
                .expectBody(LineResponse.class)
                .returnResult()
                .getResponseBody()
                ;
        return result;
    }

    @DisplayName("중복되는 Station create 테스트 코드")
    public StationResponse createStation(String stationName){

        Station station = Station.builder()
                .name(stationName)
                .build();

        StationResponse result = webTestClient.post().uri("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(station), Station.class)
                .exchange()
                .expectBody(StationResponse.class)
                .returnResult()
                .getResponseBody()
                ;
        return result;
    }

    @DisplayName("중복되는 구간등록")
    public void createEdge(Long lineId, Long sourceStationId, Long targetStationId){

        int ELAPSED_TIME = 5;
        BigDecimal DISTANCE = new BigDecimal("1.1");

        Edge edge = Edge.builder()
                .lineId(lineId)
                .elapsedTime(ELAPSED_TIME)
                .distance(DISTANCE)
                .sourceStationId(sourceStationId)
                .targetStationId(targetStationId)
                .build();

        webTestClient.post().uri("/edge")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(edge), Edge.class)
                .exchange();

    }


    @DisplayName("구간을 등록한다")
    @Test
    void createEdge() {
        Long LINE_ID = 1L;
        int ELAPSED_TIME = 5;
        BigDecimal DISTANCE = new BigDecimal("1.1");
        Long SOURCE_STATION_ID = 1L;
        Long TARGET_SATION_ID = 2L;

        Edge edge = Edge.builder()
                .lineId(resultLine.getId())
                .elapsedTime(ELAPSED_TIME)
                .distance(DISTANCE)
                .sourceStationId(resultStationA.getId())
                .targetStationId(resultStationB.getId() )
                .build();

        EntityExchangeResult<EdgeResponse> result = webTestClient.post().uri("/edge")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(edge), Edge.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(EdgeResponse.class)
                .returnResult()
                ;

        //Then
        LineDetailResponse lineDetailResponse = webTestClient.get().uri("/lines/"+resultLine.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LineDetailResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(lineDetailResponse.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(lineDetailResponse.getStations().get(1).getName()).isEqualTo("역삼역");


        //And
        StationDetailResponse resultStation = webTestClient.get().uri("/stations/" + resultStationA.getId())
                .exchange()
                .expectBody(StationDetailResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(resultStation.getLines().get(0).getName()).isEqualTo("2호선");


    }


}