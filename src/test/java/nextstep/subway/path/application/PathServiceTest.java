package nextstep.subway.path.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.application.LineService;
import nextstep.subway.path.dto.PathResponse;

@DisplayName("최단 경로를 조회하는 서비스 레이어의 단위 테스트")
public class PathServiceTest {

    @DisplayName("최단 경로를 조회하는 서비스의 메소드 호출 테스트")
    @Test
    void 최단_경로를_조회할때_서비스_메소드가_호출된다() {
        // given
        Long startId = 1L;
        Long endId = 2L;
        ShortestPathFinder shortestPathFinder = mock(ShortestPathFinder.class);
        LineService lineService = mock(LineService.class);
        when(lineService.findAllLines()).thenReturn(Collections.emptyList());
        PathService pathService = new PathService(shortestPathFinder, lineService);

        // when
        PathResponse response = pathService.findShortestPath(startId, endId, ShortestPathSearchType.DISTANCE);

        // then
        assertThat(response).isNotNull();
        verify(shortestPathFinder).findShortestPath(anyList(), anyLong(), anyLong(),
            any(ShortestPathSearchType.class));
    }

    @DisplayName("최소 시간을 조회하는 서비스의 메소드 호출 테스트")
    @Test
    void 최소_시간을_조회할때_서비스_메소드가_호출된다() {
        // given
        Long startId = 1L;
        Long endId = 2L;
        ShortestPathFinder shortestPathFinder = mock(ShortestPathFinder.class);
        LineService lineService = mock(LineService.class);
        when(lineService.findAllLines()).thenReturn(Collections.emptyList());
        PathService pathService = new PathService(shortestPathFinder, lineService);

        // when
        PathResponse response = pathService.findShortestPath(startId, endId, ShortestPathSearchType.DURATION);

        // then
        assertThat(response).isNotNull();
        verify(shortestPathFinder).findShortestPath(anyList(), anyLong(), anyLong(),
            any(ShortestPathSearchType.class));
    }
}
