package nextstep.subway.path.application;

import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
}
