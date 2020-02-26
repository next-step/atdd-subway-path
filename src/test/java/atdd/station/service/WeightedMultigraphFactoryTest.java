package atdd.station.service;

import atdd.line.domain.Line;
import atdd.line.domain.LineTest;
import atdd.line.domain.TimeTable;
import atdd.line.repository.LineRepository;
import atdd.station.domain.Duration;
import atdd.station.domain.Station;
import atdd.station.domain.StationTest;
import atdd.station.dto.PathStation;
import org.assertj.core.util.Lists;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class WeightedMultigraphFactoryTest {

    private WeightedMultigraphFactory weightedMultigraphFactory;

    private LineRepository lineRepository = mock(LineRepository.class);

    private final TimeTable timeTable = TimeTable.MAX_INTERVAL_TIME_TABLE;
    private final Line line2 = LineTest.create(1L, "2호선", timeTable, 5);
    private final Line line3 = LineTest.create(2L, "3호선", timeTable, 5);

    private final Station gangnam = StationTest.create(1L, "강남역");
    private final Station gyodae = StationTest.create(2L, "교대역");
    private final Station terminal = StationTest.create(3L, "고속터미널역");

    @BeforeEach
    void setup() {
        this.weightedMultigraphFactory = new WeightedMultigraphFactory(lineRepository);
    }

    @Test
    void create() throws Exception {
        LineTest.addStations(line2, gangnam, gyodae);
        LineTest.addStations(line3, gyodae, terminal);

        final Duration duration = new Duration(LocalTime.of(0, 5));
        line2.addSection(gangnam.getId(), gyodae.getId(), duration, 0.5);
        line3.addSection(terminal.getId(), gyodae.getId(), duration, 0.5);

        given(lineRepository.findAll()).willReturn(Lists.list(line2, line3));

        final Set<PathStation> expectedVertexes = Sets.newSet(
                PathStation.from(gangnam),
                PathStation.from(gyodae),
                PathStation.from(terminal)
        );


        final WeightedMultigraph<PathStation, DefaultWeightedEdge> result = weightedMultigraphFactory.create(terminal, gangnam);


        assertThat(result.vertexSet()).isEqualTo(expectedVertexes);
        assertThat(result.edgeSet()).hasSize(4);
        verify(lineRepository, times(1)).findAll();
    }

    @DisplayName("create - 여러번 생성해도 DB 조회는 한번만 한다.")
    @Test
    void createMany() throws Exception {
        LineTest.addStations(line2, gangnam, gyodae);
        LineTest.addStations(line3, gyodae, terminal);

        final Duration duration = new Duration(LocalTime.of(0, 5));
        line2.addSection(gangnam.getId(), gyodae.getId(), duration, 0.5);
        line3.addSection(terminal.getId(), gyodae.getId(), duration, 0.5);

        given(lineRepository.findAll()).willReturn(Lists.list(line2, line3));


        weightedMultigraphFactory.create(terminal, gangnam);
        weightedMultigraphFactory.create(terminal, gangnam);
        weightedMultigraphFactory.create(terminal, gangnam);


        verify(lineRepository, times(1)).findAll();
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