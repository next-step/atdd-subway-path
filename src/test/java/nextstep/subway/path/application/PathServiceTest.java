package nextstep.subway.path.application;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.PathStationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("지하철 경로 검색 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {
    private PathService pathService;
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    private List<Line> lines;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        stations = Lists.newArrayList(
                new Station(1L, "강남역"),
                new Station(2L, "역삼역"),
                new Station(3L, "선릉역"),
                new Station(4L, "양재역"),
                new Station(5L, "양재시민의숲역")
        );

        Line line1 = new Line("2호선", "GREEN", LocalTime.of(5, 0), LocalTime.of(23, 30), 5);
        line1.addLineStation(new LineStation(1L, null, 2, 3));
        line1.addLineStation(new LineStation(2L, 1L, 2, 3));
        line1.addLineStation(new LineStation(3L, 2L, 2, 3));

        Line line2 = new Line("신분당선", "RED", LocalTime.of(5, 0), LocalTime.of(23, 30), 5);
        line2.addLineStation(new LineStation(1L, null, 2, 3));
        line2.addLineStation(new LineStation(4L, 1L, 2, 3));
        line2.addLineStation(new LineStation(5L, 4L, 2, 3));

        lines = Lists.newArrayList(line1, line2);

        lineRepository = mock(LineRepository.class);
        stationRepository = mock(StationRepository.class);
        when(lineRepository.findAll()).thenReturn(lines);

        pathService = new PathService(lineRepository, stationRepository, new PathFinder());
    }

    @DisplayName("같은 노선의 지하철역 최단 경로를 검색한다.")
    @Test
    void findSameLinePath() {
        // given
        when(stationRepository.findAllById(Lists.newArrayList(1L, 2L, 3L)))
                .thenReturn(Lists.newArrayList(stations.get(0), stations.get(1), stations.get(2)));

        // when
        PathResponse response = pathService.findPath(1L, 3L);

        // then
        assertThat(response.getStations()).extracting(PathStationResponse::getId).containsExactly(1L, 2L, 3L);
        assertThat(response.getDistance()).isEqualTo(2 * 2);
        assertThat(response.getDuration()).isEqualTo(3 * 2);

        verify(lineRepository).findAll();
        verify(stationRepository).findAllById(Lists.newArrayList(1L, 2L, 3L));
    }

    @DisplayName("다른 노선의 지하철역 최단 경로를 검색한다.")
    @Test
    void findDifferentLinesPath() {
        // given
        when(stationRepository.findAllById(Lists.newArrayList(3L, 2L, 1L, 4L, 5L)))
                .thenReturn(Lists.newArrayList(stations.get(2), stations.get(1), stations.get(0), stations.get(3), stations.get(4)));

        // when
        PathResponse response = pathService.findPath(3L, 5L);

        assertThat(response.getStations()).extracting(PathStationResponse::getId).containsExactly(3L, 2L, 1L, 4L, 5L);
        assertThat(response.getDistance()).isEqualTo(2 * 4);
        assertThat(response.getDuration()).isEqualTo(3 * 4);

        verify(lineRepository).findAll();
        verify(stationRepository).findAllById(Lists.newArrayList(3L, 2L, 1L, 4L, 5L));
    }
}
