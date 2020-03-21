package atdd.path;

import atdd.AbstractAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EdgeAcceptanceTest extends AbstractAcceptanceTest {
    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;
    private EdgeHttpTest edgeHttpTest;


    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);
        this.edgeHttpTest = new EdgeHttpTest(webTestClient);
    }

    @Test
    void create() {
        //given
        Long sourceId = stationHttpTest.create("강남");
        Long targetId = stationHttpTest.create("역삼");
        Long lineId = lineHttpTest.create("2호선", LocalTime.of(05, 00),
                LocalTime.of(23, 50), 10).getId();
        int distance = 10;

        //when
        edgeHttpTest.create(lineId, sourceId, targetId, distance);

        //then
        assertThat(stationHttpTest.findById(sourceId).getLines()).hasSize(1);
    }

    @Test
    void delete() {
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
        webTestClient.delete().uri("/edges?lineId=" + lineId + "&stationId=" + stationId2)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        //then
        assertThat(lineHttpTest.findById(lineId).getStations().size()).isEqualTo(2);
        assertThat(stationHttpTest.findById(stationId2).getLines()).isNullOrEmpty();
    }
}