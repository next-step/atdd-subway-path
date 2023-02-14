package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.PathFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private PathFinder pathFinder;

    @Mock
    private LineService lineService;

    @InjectMocks
    private PathService pathService;

    @Test
    void 최단경로_조회() {
        //given
        PathRequest pathRequest = mock(PathRequest.class);
        Lines lines = mock(Lines.class);

        // 출발역, 도착역으로 노선 조회
        when(lineService.findByStationIds(any())).thenReturn(lines);

        //when
        pathService.findShortestPath(pathRequest);

        //then
        verify(pathFinder).searchShortestPath(pathRequest, lines.mergeSections());
    }
}