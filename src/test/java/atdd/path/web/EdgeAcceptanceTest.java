package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.path.application.dto.EdgeResponseView;
import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.application.dto.StationResponseView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EdgeAcceptanceTest extends AbstractAcceptanceTest {
    public static final String LINE_NAME = "2호선";
    private String STATION_NAME = "사당";
    private String STATION_NAME_2 = "방배";
    private String STATION_NAME_3 = "서초";
    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;
    private EdgeHttpTest edgeHttpTest;
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
        this.edgeHttpTest = new EdgeHttpTest(webTestClient);
    }

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 지하철_노선에_구간_등록을_요청한다() throws Exception {
        //given
        StationResponseView station1 = stationHttpTest.create(STATION_NAME);
        StationResponseView station2 = stationHttpTest.create(STATION_NAME_2);
        String inputJson = objectMapper.writeValueAsString(requestView);
        LineResponseView line = lineHttpTest.create(inputJson);
        Long lineId = line.getId();
        Long startId = station1.getId();
        Long endId = station2.getId();

        //when
        EdgeResponseView response
                = edgeHttpTest.createEdge(lineId, startId, endId, DISTANCE_KM, INTERVAL_TIME, objectMapper);

        //then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSource().getName()).isEqualTo("사당");
        assertThat(response.getTarget().getName()).isEqualTo("방배");
    }

    @Test
    void 지하철역_삭제를_하면_관련_엣지들도_삭제된다() throws Exception {
        //given
        StationResponseView station1 = stationHttpTest.create(STATION_NAME);
        StationResponseView station2 = stationHttpTest.create(STATION_NAME_2);
        String inputJson = objectMapper.writeValueAsString(requestView);
        LineResponseView line = lineHttpTest.create(inputJson);
        Long lineId = line.getId();
        Long startId = station1.getId();
        Long endId = station2.getId();
        edgeHttpTest.createEdge(lineId, startId, endId, DISTANCE_KM, INTERVAL_TIME, objectMapper);

        //when, then
        webTestClient.delete().uri("/edges/" + lineId + "?stationId=" + startId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}