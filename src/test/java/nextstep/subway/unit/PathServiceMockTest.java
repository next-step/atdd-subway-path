package nextstep.subway.unit;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @InjectMocks
    private PathService pathFinderService;
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

    private List<Line> 모든_라인;


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

        이호선 = new Line("2호선", "green", 교대역, 강남역, 10);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = new Line("3호선", "orange", 교대역, 남부터미널역, 2);

        삼호선.addSection(남부터미널역, 양재역, 3);

        모든_라인 = List.of(이호선, 삼호선, 신분당선);

        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(2L)).thenReturn(남부터미널역);
    }

    @Test
    void findPath() {
        // given
        when(pathFinder.find(모든_라인, 강남역, 남부터미널역)).thenReturn(new PathResponse(List.of(강남역, 교대역, 남부터미널역), 12));

        // when
        PathResponse path = pathFinderService.findPath(1L, 3L);

        assertAll(() -> {
            assertThat(path.getDistance()).isEqualTo(10);
            assertThat(path.getStations()).containsExactly(강남역, 교대역, 남부터미널역);
        });
    }
}
