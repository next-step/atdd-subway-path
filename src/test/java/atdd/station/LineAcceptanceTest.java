package atdd.station;

import atdd.station.model.CreateLineRequestView;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class LineAcceptanceTest {
    private StationTestUtils stationTestUtils;
    private LineTestUtils lineTestUtils;

    @BeforeEach
    void setUp() {
        this.stationTestUtils = new StationTestUtils(webTestClient);
        this.lineTestUtils = new LineTestUtils(webTestClient);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createLine() {
        //when
        CreateLineRequestView createLineRequestView = CreateLineRequestView.builder()
                .name("2호선")
                .startTime(LocalTime.of(5, 0))
                .endTime(LocalTime.of(23, 50))
                .intervalTime(10)
                .build();

        Line line = lineTestUtils.createLine(createLineRequestView);

        // then
        Line actualLine = createLineRequestView.toLine();
        actualLine.setId(line.getId());

        String expected = lineTestUtils.writeValueAsString(line);
        String actual = lineTestUtils.writeValueAsString(actualLine);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void findAllLines() {
        // given
        Line line = lineTestUtils.createLine("2호선",
                LocalTime.of(5, 0),
                LocalTime.of(5, 0),
                10);

        // when
        EntityExchangeResult result = webTestClient.get().uri("/lines")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(List.class).returnResult();

        //then
        String expected = lineTestUtils.writeValueAsString(result.getResponseBody());
        String actual = lineTestUtils.writeLineListAsString(Arrays.asList(line));

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void findLine() {
        // given
        Line line = lineTestUtils.createLine("2호선",
                LocalTime.of(5, 0),
                LocalTime.of(5, 0),
                10);

        // when
        EntityExchangeResult result = webTestClient.get().uri("/lines/" + line.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Line.class).returnResult();

        // then
        String expected = lineTestUtils.writeValueAsString(result.getResponseBody());
        String actual = lineTestUtils.writeValueAsString(line);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void deleteLine() {
        // given
        long lineId = lineTestUtils.createLine("2호선",
                LocalTime.of(5, 0),
                LocalTime.of(5, 0),
                10).getId();

        // when
        EntityExchangeResult result = webTestClient.delete().uri("/lines/" + lineId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().returnResult();

        // then
        Optional<Line> optionalLine = lineTestUtils.findById(lineId);
        Line line = optionalLine.isPresent() ? optionalLine.get() : null;

        assertThat(line).isNull();
    }

    @Test
    public void addEdge() {
        // given
        Station station1 = stationTestUtils.createStation("강남역");
        Station station2 = stationTestUtils.createStation("역삼역");
        Station station3 = stationTestUtils.createStation("선릉역");

        Line line = lineTestUtils.createLine("2호선",
                LocalTime.of(5, 0),
                LocalTime.of(5, 0),
                10);

        // when
        Line resultLine = lineTestUtils.addEdge(line.getId(), station1.getId(), station2.getId());

        // then
        assertThat(resultLine.getStationDtos().size()).isEqualTo(2);
        assertThat(resultLine.getStationDtos().get(0).getName()).isEqualTo("강남역");
        assertThat(resultLine.getStationDtos().get(1).getName()).isEqualTo("역삼역");
    }

    @Test
    public void deleteEdge() {
        // given
        Station station1 = stationTestUtils.createStation("강남역");
        Station station2 = stationTestUtils.createStation("역삼역");
        Station station3 = stationTestUtils.createStation("선릉역");

        Line line = lineTestUtils.createLine("2호선",
                LocalTime.of(5, 0),
                LocalTime.of(5, 0),
                10);

        lineTestUtils.addEdge(line.getId(), station1.getId(), station2.getId());
        lineTestUtils.addEdge(line.getId(), station2.getId(), station3.getId());

        // when
        webTestClient.delete().uri("/lines/" + line.getId() + "/edge?stationId=" + station2.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().returnResult();

        Line resultLine = lineTestUtils.findById(line.getId()).get();

        // then
        assertThat(resultLine.getStationDtos().size()).isEqualTo(2);
        assertThat(resultLine.getStationDtos().get(0).getName()).isEqualTo("강남역");
        assertThat(resultLine.getStationDtos().get(1).getName()).isEqualTo("선릉역");
    }
}
