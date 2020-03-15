package atdd.path;

import atdd.AbstractAcceptanceTest;
import atdd.path.dto.PathResponseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends AbstractAcceptanceTest {
    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;
    private EdgeHttpTest edgeHttpTest;
    private PathHttpTest pathHttpTest;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
        this.edgeHttpTest = new EdgeHttpTest(webTestClient);
        this.pathHttpTest = new PathHttpTest(webTestClient);
    }

    @Test
    void create() {
        //given
        Long stationId1 = stationHttpTest.create("강남");
        Long stationId2 = stationHttpTest.create("역삼");
        Long stationId3 = stationHttpTest.create("선릉");

        Long lineId = lineHttpTest.create("2호선", LocalTime.of(05, 00),
                LocalTime.of(23, 50), 10).getId();
        int distance = 10;
        edgeHttpTest.create(lineId, stationId1, stationId2, distance);
        edgeHttpTest.create(lineId, stationId2, stationId3, distance);

        //when
        PathResponseView path = pathHttpTest.findPath(stationId1, stationId3);

        //then
        assertThat(path.getStations()).hasSize(3);
        assertThat(path.getStartStationId()).isEqualTo(stationId1);
        assertThat(path.getEndStationId()).isEqualTo(stationId3);

        webTestClient.get().uri("/paths?startId=" + stationId1 + "&endId=" + stationId3)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotModified();
    }
}
