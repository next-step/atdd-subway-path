package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.map.domain.PathFinder;
import nextstep.subway.map.domain.ShortestPathEnum;
import nextstep.subway.map.dto.PathRequest;
import nextstep.subway.map.exception.*;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class PathServiceTest {
    private PathFinder pathFinder;
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private Line line;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        ReflectionTestUtils.setField(station1, "id", 1L);
        ReflectionTestUtils.setField(station2, "id", 2L);
        ReflectionTestUtils.setField(station3, "id", 3L);

        stations = Lists.newArrayList(
                station1, station2, station3
        );

        pathFinder = mock(PathFinder.class);
        lineRepository = mock(LineRepository.class);
        stationRepository = mock(StationRepository.class);
    }

    @DisplayName("최단 경로 조회")
    @Test
    void findShortestDistancePath() {
        // given
        PathService service = new PathService(pathFinder, lineRepository, stationRepository);
        PathRequest request = new PathRequest(1L, 3L, ShortestPathEnum.DISTANCE.getType());

        LineStation lineStation1 = new LineStation(1L, null, 2, 4);
        LineStation lineStation2 = new LineStation(3L, 1L, 2, 4);

        List<LineStation> lineStation = new ArrayList<>();
        lineStation.add(lineStation1);
        lineStation.add(lineStation2);

        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(line));
        when(stationRepository.existsById(anyLong())).thenReturn(true);
        when(stationRepository.findAllById(anyList())).thenReturn(stations);
        when(pathFinder.findShortestPath(anyList(), anyLong(), anyLong())).thenReturn(Lists.newArrayList(lineStation));

        // when
        service.findShortestPath(request);

        // then
        verify(lineRepository).findAll();
        verify(pathFinder).findShortestPath(anyList(), anyLong(), anyLong());
    }

    @DisplayName("최소 시간 경로 조회")
    @Test
    void findShortestDurationPath() {
        // given
        PathService service = new PathService(pathFinder, lineRepository, stationRepository);
        PathRequest request = new PathRequest(1L, 3L, ShortestPathEnum.DURATION.getType());

        LineStation lineStation1 = new LineStation(1L, null, 2, 4);
        LineStation lineStation2 = new LineStation(3L, 1L, 2, 4);

        List<LineStation> lineStation = new ArrayList<>();
        lineStation.add(lineStation1);
        lineStation.add(lineStation2);

        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(line));
        when(stationRepository.existsById(anyLong())).thenReturn(true);
        when(stationRepository.findAllById(anyList())).thenReturn(stations);
        when(pathFinder.findShortestPath(anyList(), anyLong(), anyLong())).thenReturn(Lists.newArrayList(lineStation));

        // when
        service.findShortestPath(request);

        // then
        verify(lineRepository).findAll();
        verify(pathFinder).findShortestPath(anyList(), anyLong(), anyLong());
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void findShortestPathWithSameSourceAndTarget() {
        // given
        PathService service = new PathService(pathFinder, lineRepository, stationRepository);
        PathRequest request = new PathRequest(1L, 1L, ShortestPathEnum.DISTANCE.getType());

        // when
        assertThatThrownBy(() -> service.findShortestPath(request))
                .isInstanceOf(SameSourceAndTagetException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void findShortestPathWithNotConnectedSourceAndTarget() {
        // given
        PathService service = new PathService(pathFinder, lineRepository, stationRepository);
        PathRequest request = new PathRequest(1L, 3L, ShortestPathEnum.DISTANCE.getType());
        when(stationRepository.existsById(anyLong())).thenReturn(true);

        when(pathFinder.findShortestPath(Lists.newArrayList(line), request.getSource(), request.getTarget())).thenReturn(Lists.emptyList());

        // when
        assertThatThrownBy(() -> service.findShortestPath(request))
                .isInstanceOf(NotConnectedSourceAndTargetException.class);
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void findNonExistSourceOrTarget() {
        // given
        PathService service = new PathService(pathFinder, lineRepository, stationRepository);
        PathRequest request = new PathRequest(3L, 5L, ShortestPathEnum.DISTANCE.getType());

        // when
        assertThatThrownBy(() -> service.findShortestPath(request))
                .isInstanceOf(NonExistSourceOrTargetException.class);
    }
}
