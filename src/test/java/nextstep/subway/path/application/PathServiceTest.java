package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.path.domain.PathFindType;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.ShortestPathResult;
import nextstep.subway.path.dto.PathResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("경로 조회 관련 서비스 레이어 테스트")
public class PathServiceTest {

    /**
     * 기능
     * - 인자로 받은 startStationId와 endStationId를 조회하는 기능
     * - PathFinder 도메인에 startStation과 endStation을 인자로 넘기는 메소드 호출
     * - Response객체로 변환 후 응답
     */
    @DisplayName("최단 거리 경로를 조회하는 기능 테스트")
    @Test
    void findShortestPathByDistance() {
        // given
        long startStationId = 1L;
        long endStationId = 2L;

        PathFinder pathFinder = mock(PathFinder.class);
        ShortestPathResult shortestPathResult = ShortestPathResult.empty();
        when(pathFinder.findShortestPath(anyList(), anyLong(), anyLong(), any(PathFindType.class)))
                .thenReturn(shortestPathResult);

        LineService lineService = mock(LineService.class);
        when(lineService.findAllLines()).thenReturn(Collections.emptyList());

        PathService pathService = new PathService(pathFinder, lineService);

        // when
        PathResponse pathResponse = pathService.findShortestPath(startStationId, endStationId, PathFindType.DISTANCE);

        // then
        assertThat(pathResponse).isNotNull();
        verify(pathFinder).findShortestPath(anyList(), anyLong(), anyLong(), any(PathFindType.class));
    }

    @DisplayName("최단 시간 경로를 조회하는 기능 테스트")
    @Test
    void findShortestPathByDuration() {
        // given
        long startStationId = 1L;
        long endStationId = 2L;

        PathFinder pathFinder = mock(PathFinder.class);
        ShortestPathResult shortestPathResult = ShortestPathResult.empty();
        when(pathFinder.findShortestPath(anyList(), anyLong(), anyLong(), any(PathFindType.class)))
                .thenReturn(shortestPathResult);

        LineService lineService = mock(LineService.class);
        when(lineService.findAllLines()).thenReturn(Collections.emptyList());

        PathService pathService = new PathService(pathFinder, lineService);

        // when
        PathResponse pathResponse = pathService.findShortestPath(startStationId, endStationId, PathFindType.DURATION);

        // then
        assertThat(pathResponse).isNotNull();
        verify(pathFinder).findShortestPath(anyList(), anyLong(), anyLong(), any(PathFindType.class));
    }
}
