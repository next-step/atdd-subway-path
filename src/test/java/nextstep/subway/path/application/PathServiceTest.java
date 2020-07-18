package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.path.dto.PathResponse;
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
import static org.mockito.Mockito.when;

@DisplayName("경로 탐색 서비스 유닛 테스트")
class PathServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

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
}