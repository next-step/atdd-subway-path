package atdd;

import atdd.domain.stations.Line;
import atdd.domain.stations.Section;
import atdd.domain.stations.StationLine;
import atdd.domain.stations.Stations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
public class SectionAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(SectionAcceptanceTest.class);
    private static final String BASE_URI = "/section/";
    private static final String GANGNAM_STATION_NAME = "강남역";
    private static final String YOUKSAM_STATION_NAME = "역삼역";
    private static final String LINE_2_NAME = "2호선";
    private static final String LINE_SINBUNDANG_NAME = "신분당선";
    private static final Line LINE_2 = new Line(LINE_2_NAME);
    private static final Line LINE_SINBUNDANG = new Line(LINE_SINBUNDANG_NAME);
    private static final Stations STATION_GANGNAM = new Stations(GANGNAM_STATION_NAME);
    private static final Stations STATION_YOUKSAM = new Stations(YOUKSAM_STATION_NAME);
    private static final StationLine STATIONLINE_LINE2_GANGNAM = new StationLine(STATION_GANGNAM, LINE_2);
    private static final StationLine STATIONLINE_LINE2_YOUKSAM = new StationLine(STATION_YOUKSAM, LINE_2);
    private static final StationLine STATIONLINE_SINBUNDANG_GANGNAM = new StationLine(STATION_GANGNAM, LINE_SINBUNDANG);
    private static final List<StationLine> GANGNAM_LINES
            = new ArrayList<>(Arrays.asList(STATIONLINE_LINE2_GANGNAM, STATIONLINE_SINBUNDANG_GANGNAM));
    private static final List<StationLine> YOUKSAM_LINES
            = new ArrayList<>(Arrays.asList(STATIONLINE_LINE2_YOUKSAM));
    private static final List<StationLine> STATIONLINES_2
            = new ArrayList<>(Arrays.asList(STATIONLINE_LINE2_GANGNAM, STATIONLINE_LINE2_YOUKSAM));

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("지하철 구간 등록이 제대로 되는가?")
    @Test
    public void createSection() {
        //given

        //when, then
        webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Section.builder()
                        .line(LINE_2)
                        .source(STATION_GANGNAM)
                        .target(STATION_YOUKSAM)
                        .build()), Section.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody()
                .jsonPath("$.line.name").isEqualTo(LINE_2_NAME)
                .jsonPath("$.source.name").isEqualTo(GANGNAM_STATION_NAME)
                .jsonPath("$.target.name").isEqualTo(YOUKSAM_STATION_NAME);
    }

    public String createTestSection(Stations source, Stations target) {
        return webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(Section.builder()
                        .line(LINE_2)
                        .source(STATION_GANGNAM)
                        .target(STATION_YOUKSAM)
                        .build()), Section.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody()
                .jsonPath("$.source.name").isEqualTo(GANGNAM_STATION_NAME)
                .jsonPath("$.target.name").isEqualTo(YOUKSAM_STATION_NAME)
                .returnResult()
                .getResponseHeaders()
                .getLocation().getPath();
    }
}