package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.PathService;
import nextstep.subway.service.dto.PathDto;
import nextstep.subway.service.dto.StationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nextstep.subway.helper.fixture.LineFixture.신분당선_엔티티;
import static nextstep.subway.helper.fixture.LineFixture.이호선_엔티티;
import static nextstep.subway.helper.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private PathFinder pathFinder;
    @InjectMocks
    private PathService pathService;

    @Test
    @DisplayName("findShortestPath를 호출하면 path 정보를 조회할 수 있다.")
    void succeed() {
        final Station 출발역 = 논현역_엔티티;
        final Station 도착역 = 역삼역_엔티티;
        final Long 출발역ID = 1L;
        final Long 도착역ID = 3L;

        // given
        Line 신분당선 = 신분당선_엔티티(강남역_엔티티, 출발역);
        Line 이호선 = 이호선_엔티티(강남역_엔티티, 도착역);

        List<Line> 모든_노선 = List.of(신분당선, 이호선);
        Path 예상_경로 = new Path(List.of(출발역, 강남역_엔티티, 도착역), 5);

        when(stationRepository.findById(출발역ID)).thenReturn(Optional.of(출발역));
        when(stationRepository.findById(도착역ID)).thenReturn(Optional.of(도착역));
        when(lineRepository.findAll()).thenReturn(모든_노선);
        when(pathFinder.findShortestPathAndItsDistance(anyList(), eq(출발역), eq(도착역))).thenReturn(예상_경로);

        // when
        PathDto path = pathService.findShortestPath(출발역ID, 도착역ID);

        // then
        PathDto 경로_리턴값 = new PathDto(StationDto.from(예상_경로.getStations()), 예상_경로.getDistance());
        List<String> 경로_역들_이름 = 경로_리턴값.getStations()
                .stream()
                .map(StationDto::getName)
                .collect(Collectors.toList());

        assertThat(path.getDistance()).isEqualTo(경로_리턴값.getDistance());
        assertThat(path.getStations()
                .stream()
                .map(StationDto::getName)
                .collect(Collectors.toList())
        ).containsExactlyElementsOf(경로_역들_이름);

        verify(stationRepository, times(1)).findById(출발역ID);
        verify(stationRepository, times(1)).findById(도착역ID);
        verify(lineRepository, times(1)).findAll();
    }
}
