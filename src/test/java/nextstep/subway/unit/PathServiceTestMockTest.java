package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.ShortestPathFinder;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@DisplayName("최소 경로 찾기의 대한 Mock 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTestMockTest {

    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    @Mock
    private ShortestPathFinder shortestPathFinder;
    @InjectMocks
    private PathService pathService;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {

        교대역 = 역_생성(1L, "교대역");
        강남역 = 역_생성(2L, "강남역");
        양재역 = 역_생성(3L, "양재역");
        남부터미널역 = 역_생성(4L, "남부터미널역");

        이호선 = 노선_생성(1L, "2호선", "green", 교대역, 강남역, 10);
        신분당선 = 노선_생성(2L, "신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 노선_생성(3L, "3호선", "orange", 교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @DisplayName("최소 경로를 조회한다.")
    @Test
    void showRoutes() {

        final List<Line> 전체_노선_목록 = List.of(이호선, 신분당선, 삼호선);
        final Path 경로 = Path.of(전체_노선_목록, 교대역, 양재역);
        final PathResult 경로_결과 = PathResult.of(List.of(교대역, 남부터미널역, 양재역), 5);

        when(stationService.findById(anyLong())).thenReturn(교대역).thenReturn(양재역);
        when(lineService.findAllLines()).thenReturn(전체_노선_목록);
        when(shortestPathFinder.findRoute(경로)).thenReturn(경로_결과);

        final PathResponse pathResponse = pathService.showRoutes(교대역.getId(), 양재역.getId());

        final InOrder inOrder = inOrder(stationService, lineService, shortestPathFinder);
        inOrder.verify(stationService, times(2)).findById(anyLong());
        inOrder.verify(lineService, times(1)).findAllLines();
        inOrder.verify(shortestPathFinder, times(1)).findRoute(any());

        assertAll(
                () -> assertThat(pathResponse.getStations()).hasSize(3),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(5)
        );
    }

    private Line 노선_생성(final Long id, final String name, final String color, final Station upStation, final Station downStation, final Integer distance) {
        final Line line = new Line(name, color, upStation, downStation, distance);
        ReflectionTestUtils.setField(line, "id", id);
        return line;
    }

    private Station 역_생성(final Long id, final String name) {
        final Station 역 = new Station(name);
        ReflectionTestUtils.setField(역, "id", id);
        return 역;
    }
}
