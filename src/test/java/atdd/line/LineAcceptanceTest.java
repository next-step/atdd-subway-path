package atdd.line;

import atdd.Edge.Edge;
import atdd.station.Station;
import atdd.station.StationAcceptanceTest;
import atdd.station.StationResponse;
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

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
public class LineAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private LineService lineService;


    @DisplayName("노선을 등록한다.")
    @Test
    public void createLine(){
        String STATION_NAME = "2호선";
        LocalTime START_TIME = LocalTime.of(5,0);
        LocalTime END_TIME = LocalTime.of(23, 30);
        int INTERVAL_TIME = 10;
        int EXTRA_FARE = 0;

        Line line = Line.builder()
                .name(STATION_NAME)
                .start_time(START_TIME)
                .end_time(END_TIME)
                .interval_time(INTERVAL_TIME)
                .extra_fare(EXTRA_FARE)
                .build();

        EntityExchangeResult<LineResponse> result = webTestClient.post().uri("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(line), Line.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(LineResponse.class)
                .returnResult()
                ;

        String location = result.getResponseHeaders().getLocation().getPath();
        LineResponse resultLine = result.getResponseBody();

        assertThat(location).isEqualTo("/lines/"+ resultLine.getId());
        assertThat(resultLine.getName()).isEqualTo(STATION_NAME);
        assertThat(resultLine.getStart_time()).isEqualTo(START_TIME);
        assertThat(resultLine.getEnd_time()).isEqualTo(END_TIME);
        assertThat(resultLine.getInterval_time()).isEqualTo(INTERVAL_TIME);
        assertThat(resultLine.getExtra_fare()).isEqualTo(EXTRA_FARE);

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
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
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

    @DisplayName("지하철 노선 정보를 조회한다")
    @Test
    public void findLineById(){
        String LINE_NAME = "2호선";

        StationResponse resultStationA = createStation("강남역");
        StationResponse resultStationB = createStation("교대역");
        StationResponse resultStationC = createStation("서초역");

        LineResponse resultLine = createLine(LINE_NAME);

        createEdge(resultLine.getId(), resultStationA.getId(), resultStationB.getId());
        createEdge(resultLine.getId(), resultStationB.getId(), resultStationC.getId());

        LineDetailResponse lineDetailResponse = webTestClient.get().uri("/lines/"+resultLine.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LineDetailResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(lineDetailResponse.getLine().getName()).isEqualTo(LINE_NAME);
        assertThat(lineDetailResponse.getStations().size()).isEqualTo(3);


    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    public void getLines(){
        String LINE_NAME="2호선";
        LineResponse createdLine = createLine(LINE_NAME);

        LineListResponse lines = webTestClient.get().uri("/lines")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LineListResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(lines.getLines().get(0).getName()).isEqualTo(createdLine.getName());
    }

}
