package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.EdgeRequestViewFromClient;
import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.application.dto.StationResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.LocalTime;

public class EdgeAcceptanceTest extends AbstractAcceptanceTest {
    public static final String LINE_NAME = "2호선";
    public static final String LINE_NAME_2 = "4호선";
    private String STATION_NAME = "사당";
    private String STATION_NAME_2 = "방배";
    private String STATION_NAME_3 = "서초";
    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;
    public static final LocalTime START_TIME = LocalTime.of(5, 00);
    public static final LocalTime END_TIME = LocalTime.of(23, 50);
    public static final int INTERVAL_TIME = 10;
    public static final int DISTANCE_KM = 5;
    public LineRequestView requestView = LineRequestView.builder()
            .name(LINE_NAME)
            .startTime(START_TIME)
            .endTime(END_TIME)
            .interval(INTERVAL_TIME)
            .build();

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
    }

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("지하철 노선에 구간 등록을 요청한다.")
    @Test
    void addEdge() throws Exception {
        //given
        StationResponseView stationResponseView = stationHttpTest.create(STATION_NAME);
        StationResponseView stationResponseView2 = stationHttpTest.create(STATION_NAME_2);
        String input = objectMapper.writeValueAsString(requestView);
        LineResponseView lineResponseView = lineHttpTest.create(input);

        //when
        EdgeRequestViewFromClient edgeRequestViewFromClient
                = EdgeRequestViewFromClient.builder()
                .lineId(1L)
                .sourceId(1L)
                .targetId(2L)
                .distance(DISTANCE_KM)
                .timeToTake(INTERVAL_TIME)
                .build();
        String value = objectMapper.writeValueAsString(edgeRequestViewFromClient);

        // then
        webTestClient.post().uri("/edges")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(value), String.class)
                .exchange()
                .expectStatus().isCreated();
        //assertThat(responseBody.getSource().getName()).isEqualTo(STATION_NAME);
        // assertThat(responseBody.getSource().getLines().size()).isEqualTo(1);
//        assertThat(responseBody.getLine().bringStations().size()).isEqualTo(2);
    }
}
