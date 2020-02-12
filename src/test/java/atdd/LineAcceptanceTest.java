package atdd;

import atdd.domain.stations.Line;
import atdd.domain.stations.StationLine;
import atdd.domain.stations.Stations;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LineAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);
    private static final String BASE_URI = "/line/";
    private static final String GANGNAM_STATION_NAME = "강남역";
    private static final String YOUKSAM_STATION_NAME = "역삼역";
    private static final String LINE_2_NAME="2호선";
    private static final String LINE_SINBUNDANG_NAME="신분당선";
    private static final Line LINE_2=new Line(LINE_2_NAME);
    private static final Line LINE_SINBUNDANG=new Line(LINE_SINBUNDANG_NAME);
    private static final Stations STATION_GANGNAM=new Stations(GANGNAM_STATION_NAME);
    private static final Stations STATION_YOUKSAM=new Stations(YOUKSAM_STATION_NAME);
    private static final StationLine STATIONLINE_LINE2_GANGNAM=new StationLine(STATION_GANGNAM, LINE_2);
    private static final StationLine STATIONLINE_LINE2_YOUKSAM=new StationLine(STATION_YOUKSAM, LINE_2);
    private static final StationLine STATIONLINE_SINBUNDANG_GANGNAM=new StationLine(STATION_GANGNAM, LINE_SINBUNDANG);
    private static final List<StationLine> GANGNAM_LINES
            =new ArrayList<>(Arrays.asList(STATIONLINE_LINE2_GANGNAM, STATIONLINE_SINBUNDANG_GANGNAM));
    private static final List<StationLine> YOUKSAM_LINES
            =new ArrayList<>(Arrays.asList(STATIONLINE_LINE2_YOUKSAM));
    private static final List<StationLine> STATIONLINES_2
            =new ArrayList<>(Arrays.asList(STATIONLINE_LINE2_GANGNAM, STATIONLINE_LINE2_YOUKSAM));

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("지히철 노선 등록이 제대로 되는가")
    @Test
    public void createLine() {
        //given

        //when, then
        webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Line.builder()
                        .name(LINE_2_NAME)
                        .stationLines(STATIONLINES_2)
                        .build()), Line.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(LINE_2_NAME)
                .jsonPath("$.stations[0].name").isEqualTo(GANGNAM_STATION_NAME)
                .jsonPath("$.stations[1].name").isEqualTo(YOUKSAM_STATION_NAME)
                .jsonPath("$.startTime").isEqualTo("05:00")
                .jsonPath("$.endTime").isEqualTo("23:50")
                .jsonPath("$.intervalTime").isEqualTo("10");
    }

    public String createTestLine(String name, List<StationLine> stationLineList) {
        return webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Line.builder()
                        .name(name)
                        .stationLines(stationLineList)
                        .build()), Line.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody().jsonPath("$.name").isEqualTo(name)
                .returnResult()
                .getResponseHeaders()
                .getLocation().getPath();
    }

    @DisplayName("지하철 노선 삭제가 제대로 되는가")
    @Test
    public void deleteLine() {
        //given
        String location=createTestLine(LINE_2_NAME, STATIONLINES_2);

        //when, then
        webTestClient.delete().uri(location)
                .exchange()
                .expectStatus().isOk();
    }

    @DisplayName("지하철 노선 목록 조회가 제대로 되는가")
    @Test
    public void readList() {
        //given
        createTestLine(LINE_2_NAME, STATIONLINES_2);
        createTestLine(LINE_SINBUNDANG_NAME, GANGNAM_LINES);

        //when, then
        webTestClient.get().uri(BASE_URI)
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody().jsonPath("$.lines[0].name").isEqualTo(LINE_2_NAME)
                .jsonPath("$.lines[1].name").isEqualTo(LINE_SINBUNDANG_NAME);
    }

    @DisplayName("지하철 노선 상세 조회가 제대로 되는가")
    @Test
    public void read() {
        //given
        String location=createTestLine(LINE_2_NAME, GANGNAM_LINES);

        //when, then
        webTestClient.get().uri(location)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo(LINE_2_NAME);
    }
}
