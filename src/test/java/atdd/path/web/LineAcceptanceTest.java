package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AbstractAcceptanceTest {
    public static final String LINE_BASE_URI = "/lines";
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

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
    }

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("지하철 노선 등록을 요청한다.")
    @Test
    void create() throws Exception {
        //given
        LineRequestView requestView = LineRequestView.builder()
                .name(LINE_NAME)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .interval(INTERVAL_TIME)
                .build();
        String inputJson = objectMapper.writeValueAsString(requestView);

        //when
        LineResponseView responseView = lineHttpTest.create(inputJson);

        //then
        assertThat(responseView.getId()).isEqualTo(1L);
        assertThat(responseView.getName()).isEqualTo(LINE_NAME);
    }

    @DisplayName("지하철 노선 삭제를 요청한다.")
    @Test
    void delete() throws Exception {
        //given
        LineRequestView requestView = LineRequestView.builder()
                .name(LINE_NAME)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .interval(INTERVAL_TIME)
                .build();
        String inputJson = objectMapper.writeValueAsString(requestView);
        LineResponseView responseView = lineHttpTest.create(inputJson);

        //when, then
        webTestClient.delete().uri(LINE_BASE_URI + "/" + responseView.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @DisplayName("지하철 노선 정보 조회를 요청한다.")
    @Test
    void retrieve() throws Exception {
        //given
        LineRequestView requestView = LineRequestView.builder()
                .name(LINE_NAME)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .interval(INTERVAL_TIME)
                .build();
        String inputJson = objectMapper.writeValueAsString(requestView);
        LineResponseView responseView = lineHttpTest.create(inputJson);

        //when, then
        webTestClient.get().uri(LINE_BASE_URI + "/" + responseView.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo(LINE_NAME)
                .jsonPath("$.interval").isEqualTo(INTERVAL_TIME);
    }

    @DisplayName("지하철 노선 목록 조회를 요청한다.")
    @Test
    void showAll() throws Exception {
        //given
        int theNumberOfLines = 2;
        LineRequestView requestView = LineRequestView.builder()
                .name(LINE_NAME)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .interval(INTERVAL_TIME)
                .build();
        LineRequestView requestView2 = LineRequestView.builder()
                .name(LINE_NAME_2)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .interval(INTERVAL_TIME)
                .build();
        String inputJson = objectMapper.writeValueAsString(requestView);
        String inputJson2 = objectMapper.writeValueAsString(requestView2);

        //when
        lineHttpTest.create(inputJson);
        lineHttpTest.create(inputJson2);

        //then
        webTestClient.get().uri(LINE_BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBodyList(LineListResponseView.class)
                .hasSize(theNumberOfLines);
    }

//    @DisplayName("지하철 노선에 구간 등록을 요청한다.")
//    @Test
//    void addEdge() throws Exception {
//        //given
//        StationResponseView responseView1 = stationHttpTest.create(STATION_NAME);
//        StationResponseView responseView2 = stationHttpTest.create(STATION_NAME_2);
//        LineRequestView requestView = LineRequestView.builder()
//                .name(LINE_NAME)
//                .startTime(START_TIME)
//                .endTime(END_TIME)
//                .interval(INTERVAL_TIME)
//                .build();
//        String input = objectMapper.writeValueAsString(requestView);
//        LineResponseView responseView = lineHttpTest.create(input);
//
//        //when
//        EdgeRequestView edgeRequestView = EdgeRequestView.builder()
//                .lineId(responseView.getId())
//                .sourceId(responseView1.getId())
//                .targetId(responseView2.getId())
//                .build();
//        String inputJson = objectMapper.writeValueAsString(edgeRequestView);
//
//        // then
//        EdgeResponseView responseBody = webTestClient.post().uri("/lines/" + responseView.getId() + "/edges")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(Mono.just(inputJson), String.class)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(EdgeResponseView.class)
//                .returnResult()
//                .getResponseBody();
//
//        assertThat(responseBody.getId()).isEqualTo(1L);
//        assertThat(responseBody.getSource().getName()).isEqualTo(STATION_NAME);
//        assertThat(responseBody.getSource().getLines().get(0).getName()).isEqualTo(LINE_NAME);
//        assertThat(responseBody.getLine().getStations().size()).isEqualTo(2);
//    }
}