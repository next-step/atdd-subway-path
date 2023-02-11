package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private LineService lineService;

    @InjectMocks
    private PathService pathService;

    @Test
    void 최단경로_조회() {
        //given
        PathRequest pathRequest = mock(PathRequest.class);
        Lines lines = mock(Lines.class);
        PathFinder pathFinder = mock(PathFinder.class);

        // 출발역, 도착역으로 노선 조회
        when(lineService.findByStationIds(any())).thenReturn(lines);
        // 노선에서 경로탐색기 생성
        when(lines.toPathFinder()).thenReturn(pathFinder);

        //when
        pathService.findShortestPath(pathRequest);

        //then
        verify(pathFinder).findShortest(lines);
    }
}