package nextstep.subway.path.application;

import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    @DisplayName("최단 경로를 조회하는 기능 테스트")
    @Test
    void findShortestPath() {
        // given
        long startStationId = 1L;
        long endStationId = 2L;
        PathFinder pathFinder = mock(PathFinder.class);

        StationRepository stationRepository = mock(StationRepository.class);
        when(stationRepository.findById(1L)).thenReturn(any());

        PathService pathService = new PathService(pathFinder, stationRepository);

        // when
        PathResponse pathResponse = pathService.findShortestPath(startStationId, endStationId);

        // then
        assertThat(pathResponse).isNotNull();
        verify(pathFinder).findShortestDistance(any(), any());
    }
}
