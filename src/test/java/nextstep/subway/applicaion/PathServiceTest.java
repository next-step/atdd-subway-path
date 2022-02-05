package nextstep.subway.applicaion;

import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("경로 서비스(PathService)")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private PathService pathService;

    @DisplayName("출발역과 도착역이 같은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathBySameSourceAndTarget( ) {
        final Station station = new Station("교대역");
        given(stationRepository.findById(any())).willReturn(Optional.of(station));

        assertThatThrownBy(() -> pathService.findShortestPath(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("source and target stations conflict");
    }
}
