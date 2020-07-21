package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    private PathService pathService;

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        // given
        this.pathService = new PathService(lineRepository, stationRepository);

        Line line1 = new Line("2호선", "GREEN", LocalTime.of(6, 30), LocalTime.of(23, 0), 5);
        Line line2 = new Line("신분당선", "RED", LocalTime.of(6, 30), LocalTime.of(23, 0), 5);

        line1.addLineStation(new LineStation(1L, null, 0, 0));
        line1.addLineStation(new LineStation(2L, 1L, 5, 10));
        line1.addLineStation(new LineStation(3L, 2L, 5, 10));

        line2.addLineStation(new LineStation(1L, null, 0, 0));
        line2.addLineStation(new LineStation(4L, 1L, 5, 10));

        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");
        Station station4 = new Station("양재역");
        ReflectionTestUtils.setField(station1, "id", 1L);
        ReflectionTestUtils.setField(station2, "id", 2L);
        ReflectionTestUtils.setField(station3, "id", 3L);
        ReflectionTestUtils.setField(station4, "id", 4L);

        List<Line> lines = Arrays.asList(line1, line2);
        List<Station> stations = Arrays.asList(station1, station2, station3, station4);

        when(lineRepository.findAll()).thenReturn(lines);
        when(stationRepository.findByIdIn(anyList())).thenReturn(stations);
    }

    @Test
    void searchShortestPath() {
        // when
        PathResponse path = pathService.searchShortestPath(3L, 4L);

        // then
        assertThat(path).isNotNull();
        assertThat(path.getDistance()).isEqualTo(15);
        assertThat(path.getDuration()).isEqualTo(30);
        assertThat(path.getStations().stream().map(StationResponse::getId)).containsExactly(3L, 2L, 1L, 4L);
    }
}