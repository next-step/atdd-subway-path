package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.ui.FindType;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("경로 탐색 서비스 유닛 테스트")
class PathServiceTest {

    @Mock
    @Deprecated
    private LineRepository lineRepository;

    @Mock
    @Deprecated
    private StationRepository stationRepository;

    @Mock
    private LineService lineService;

    private PathService pathService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        pathService = new PathService(lineRepository, stationRepository);
    }

    @DisplayName("경로를 잘 찾는지 검사한다.")
    @Test
    void findPath() {

        // stubbing
        final Line line = new Line("이호선", "GREEN", LocalTime.now(), LocalTime.now(), 5);
        line.addLineStation(new LineStation(1L, null, 10, 10));
        line.addLineStation(new LineStation(2L, 1L, 10, 10));
        line.addLineStation(new LineStation(3L, 2L, 10, 10));
        when(lineRepository.findAll()).thenReturn(List.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(new ArrayList<>());

        // when
        final PathResponse pathResponse = pathService.findShortestPath(1, 3);

        // then
        assertThat(pathResponse).isNotNull();
    }

    @DisplayName("주어진 탐색 타입을 가지고 잘 찾는지 검사한다")
    @Test
    void findPathWithTypes() {

        // when
        final PathResponse pathResponse = pathService.findShortestPath(1, 3, FindType.DISTANCE);

        // then
        assertThat(pathResponse).isNotNull();
        verify(lineService).findAllLines();
    }
}