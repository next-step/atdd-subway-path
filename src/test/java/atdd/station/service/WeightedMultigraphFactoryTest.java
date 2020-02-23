package atdd.station.service;

import atdd.line.domain.Line;
import atdd.line.domain.LineTest;
import atdd.line.domain.TimeTable;
import atdd.station.domain.Duration;
import atdd.station.domain.Station;
import atdd.station.domain.StationTest;
import atdd.station.dto.PathStation;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeightedMultigraphFactoryTest {

    private WeightedMultigraphFactory weightedMultigraphFactory;

    @BeforeEach
    void setup() {
        this.weightedMultigraphFactory = new WeightedMultigraphFactory();
    }

    @Test
    void create() throws Exception {
        final TimeTable timeTable = TimeTable.MAX_INTERVAL_TIME_TABLE;
        final Line line2 = LineTest.create(1L, "2호선", timeTable, 5);
        final Line line3 = LineTest.create(2L, "3호선", timeTable, 5);

        final Station gangnam = StationTest.create(1L, "강남역");
        final Station gyodae = StationTest.create(2L, "교대역");
        final Station terminal = StationTest.create(3L, "고속터미널역");

        LineTest.addStations(line2, gangnam, gyodae);
        LineTest.addStations(line3, gyodae, terminal);

        final Duration duration = new Duration(LocalTime.of(0, 5));
        line2.addSection(gangnam.getId(), gyodae.getId(), duration, 0.5);
        line3.addSection(terminal.getId(), gyodae.getId(), duration, 0.5);

        final Set<PathStation> expectedVertexes = Sets.newSet(
                PathStation.from(gangnam),
                PathStation.from(gyodae),
                PathStation.from(terminal)
        );


        final WeightedMultigraph<PathStation, DefaultWeightedEdge> result = weightedMultigraphFactory.create(terminal, gangnam);


        assertThat(result.vertexSet()).isEqualTo(expectedVertexes);
        assertThat(result.edgeSet()).hasSize(4);
    }

    @DisplayName("create - 출발역은 필수")
    @Test
    void createNullStartStation() throws Exception {
        final Station startStation = null;
        final Station endStation = StationTest.create(1L, "강남역");

        assertThatThrownBy(() -> weightedMultigraphFactory.create(startStation, endStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역은 필수 입니다.");
    }

    @DisplayName("create - 도착역은 필수")
    @Test
    void createNullEndStation() throws Exception {
        final Station startStation = StationTest.create(1L, "강남역");
        final Station endStation = null;

        assertThatThrownBy(() -> weightedMultigraphFactory.create(startStation, endStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("도착역은 필수 입니다.");
    }

    @DisplayName("create - 출발역에 등록된 노선이 없으면 에러")
    @Test
    void createWithEmtpyStartStationLine() throws Exception {
        final Line line3 = LineTest.create(2L, "3호선", TimeTable.MAX_INTERVAL_TIME_TABLE, 5);
        final Station gangnam = StationTest.create(1L, "강남역");
        final Station gyodae = StationTest.create(2L, "교대역");

        gyodae.addLine(line3);

        assertThatThrownBy(() -> weightedMultigraphFactory.create(gangnam, gyodae))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역에 등록된 노선이 없습니다. 역이름 : [강남역]");
    }

    @DisplayName("create - 도착역에 등록된 노선이 없으면 에러")
    @Test
    void createWithEmtpyEndStationLine() throws Exception {
        final Line line2 = LineTest.create(1L, "2호선", TimeTable.MAX_INTERVAL_TIME_TABLE, 5);
        final Station gangnam = StationTest.create(1L, "강남역");
        final Station gyodae = StationTest.create(2L, "교대역");

        gangnam.addLine(line2);

        assertThatThrownBy(() -> weightedMultigraphFactory.create(gangnam, gyodae))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("도착역에 등록된 노선이 없습니다. 역이름 : [교대역]");
    }

}