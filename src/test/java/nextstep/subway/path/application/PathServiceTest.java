package nextstep.subway.path.application;

import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathRequest;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.SameSourceAndTagetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class PathServiceTest {
    private PathFinder pathFinder;
    private LineRepository lineRepository;

    @BeforeEach
    void setUp() {
        pathFinder = mock(PathFinder.class);
        lineRepository = mock(LineRepository.class);
    }

    @DisplayName("최단 경로 조회")
    @Test
    void findShortestPath() {
        // given
        PathService service = new PathService(pathFinder, lineRepository);

        // when
        service.findShortestPath(new PathRequest(1L, 3L));

        // then
        verify(lineRepository).findAll();
        verify(pathFinder).findShortestPath(anyList(), anyLong(), anyLong());
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void findShortestPathWithSameSourceAndTarget() {
        // given
        PathService service = new PathService(pathFinder, lineRepository);
        PathRequest request = new PathRequest(1L, 1L);

        // when
        assertThatThrownBy(() -> service.findShortestPath(request))
                .isInstanceOf(SameSourceAndTagetException.class);
    }
}
