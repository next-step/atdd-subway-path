package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathFinderService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.*;
import nextstep.subway.domain.dto.PathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("지하철 경로 조회 서비스 Mock 단위 테스트")
public class PathServiceMockTest {

    @InjectMocks
    private PathFinderService pathFinderService;

    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    @Mock
    private PathFinder pathFinder;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    private List<Line> 모든_노선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void setUp() {
        // given
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green", new Section(교대역, 강남역, 10));
        신분당선 = new Line("신분당선", "red", new Section(강남역, 양재역, 10));
        삼호선 = new Line("3호선", "orange", new Section(교대역, 남부터미널역, 2));

        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        모든_노선 = List.of(이호선, 신분당선, 삼호선);

        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(3L)).thenReturn(양재역);
        when(lineService.findAll()).thenReturn(모든_노선);
    }

    @Test
    @DisplayName("경로 조회")
    void findPath() {
        // given
        when(pathFinder.find(교대역, 양재역)).thenReturn(new PathResponse(List.of(교대역, 남부터미널역, 양재역), 5));

        // when
        PathResponse path = pathFinderService.findPath(1L, 3L);

        // then
        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(5),
                () -> assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역)
        );
    }
}
