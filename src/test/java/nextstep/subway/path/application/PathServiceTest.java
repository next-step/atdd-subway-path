package nextstep.subway.path.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.application.LineService;
import nextstep.subway.path.dto.PathResponse;

@DisplayName("최단 경로를 조회하는 서비스 레이어의 단위 테스트")
public class PathServiceTest {

    @DisplayName("최단 경로를 조회하는 서비스 테스트")
    @Test
    void 최단_경로를_조회한다() {
        // given
        Long startId = 1L;
        Long endId = 2L;
        ShortestPathFinder shortestPathFinder = mock(ShortestPathFinder.class);
        LineService lineService = mock(LineService.class);
        PathService pathService = new PathService(shortestPathFinder, lineService);

        // when
        PathResponse response = pathService.findShortestPath(startId, endId, ShortestPathSearchType.DURATION);

        // then
        assertThat(response).isNotNull();
        verify(shortestPathFinder).findShortestDistance(anyList(), anyLong(), anyLong(),
            any(ShortestPathSearchType.class));
    }
}
