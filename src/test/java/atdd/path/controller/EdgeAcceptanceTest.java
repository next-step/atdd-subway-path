package atdd.path.controller;

import atdd.AbstractAcceptanceTest;
import atdd.path.domain.dto.LineDto;
import atdd.path.domain.dto.EdgeDto;
import atdd.path.domain.dto.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

public class EdgeAcceptanceTest extends AbstractAcceptanceTest {

    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;
    private EdgeHttpTest edgeHttpTest;

    private LineDto secondLine;
    private StationDto gangnamStation;
    private StationDto yeoksamStation;
    private StationDto seonneungStation;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
        this.edgeHttpTest = new EdgeHttpTest(webTestClient);

        // given
        this.gangnamStation = stationHttpTest.createStationTest("강남역").getResponseBody();
        this.yeoksamStation = stationHttpTest.createStationTest("역삼역").getResponseBody();
        this.seonneungStation = stationHttpTest.createStationTest("선릉역").getResponseBody();

        this.secondLine = lineHttpTest.createLineTest().getResponseBody();
    }

    @Test
    public void 지하철노선에_지하철_구간을_등록() {
        // when
        String input = "{" +
                "\"lineId\": " + this.secondLine.getId() + "," +
                "\"sourceStationId\": " + this.gangnamStation.getId() + "," +
                "\"targetStationId\": " + this.yeoksamStation.getId() +
                "}";

        webTestClient.post().uri("/edges")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(input), String.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(EdgeDto.class)
                .isEqualTo(EdgeDto.builder()
                        .lineId(secondLine.getId())
                        .sourceStationId(gangnamStation.getId())
                        .targetStationId(yeoksamStation.getId())
                        .build());

        // then
        StationDto station = stationHttpTest.findStationByIdTest(secondLine.getId()).getResponseBody();
        assertThat(station.getLines().size()).isEqualTo(1);
    }


    @Test
    public void 지하철노선에_지하철_구간을_제외() {
        // given
        edgeHttpTest.createEdge(this.secondLine.getId(), this.yeoksamStation.getId(), this.seonneungStation.getId(), 3, 3).getResponseBody();
        edgeHttpTest.createEdge(this.secondLine.getId(), this.gangnamStation.getId(), this.yeoksamStation.getId(), 2, 2).getResponseBody();

        // when
        webTestClient.delete().uri("/edges/{lineId}/{stationId}", this.secondLine.getId(), this.yeoksamStation.getId())
                .exchange()
                .expectStatus().isNoContent();

        // then
        StationDto station = stationHttpTest.findStationByIdTest(this.yeoksamStation.getId()).getResponseBody();
        assertThat(station.getLines().size()).isEqualTo(0);

        LineDto line = lineHttpTest.findLineByTest(this.secondLine.getId()).getResponseBody();
        assertThat(line.getStations().size()).isEqualTo(2);
    }
}
