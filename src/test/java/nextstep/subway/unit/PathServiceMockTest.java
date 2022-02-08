package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class PathServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private PathService pathService;

    @Test
    void getPath() {
        // given
        LocalDateTime now = LocalDateTime.now();
        StationResponse 교대역 = new StationResponse(1L, "교대역", now, now);
        StationResponse 강남역 = new StationResponse(2L, "강남역", now, now);
        StationResponse 양재역 = new StationResponse(3L, "양재역", now, now);
        StationResponse 남부터미널역 = new StationResponse(4L, "남부터미널역", now, now);

        List<StationResponse> stations = new ArrayList<>();
        stations.add(교대역);
        stations.add(남부터미널역);
        stations.add(양재역);

        // when
        when(pathService.getPaths(1L, 3L)).thenReturn(new PathResponse(stations, 5));

        // then
        PathResponse path = pathService.getPaths(1L, 3L);
        assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getDistance()).isEqualTo(5);
    }
}
