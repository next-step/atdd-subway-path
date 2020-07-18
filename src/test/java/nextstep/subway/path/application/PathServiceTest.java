package nextstep.subway.path.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.path.dto.ShortestPathResponse;
import nextstep.subway.station.domain.StationRepository;

@DisplayName("최단 경로를 조회하는 서비스 레이어의 단위 테스트")
public class PathServiceTest {

    @DisplayName("최단 경로를 조회하는 서비스 테스트")
    @Test
    void 최단_경로를_조회한다() {
        // given
        Long startId = 1L;
        Long endId = 2L;
        ShortestPathFinder shortestPathFinder = mock(ShortestPathFinder.class);
        StationRepository stationRepository = mock(StationRepository.class);
        PathService pathService = new PathService(shortestPathFinder, stationRepository);

        // when
        ShortestPathResponse response = pathService.findShortestPath(startId, endId);

        // then
        assertThat(response).isNotNull();
        verify(shortestPathFinder).findShortestDistance(any(), any());
    }
}
