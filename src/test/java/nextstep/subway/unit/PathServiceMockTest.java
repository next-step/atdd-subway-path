package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @InjectMocks
    private PathService pathService;
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
    private List<Line> 전체_노선_목록;

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

        전체_노선_목록 = List.of(이호선, 신분당선, 삼호선);

        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(2L)).thenReturn(남부터미널역);
        when(lineService.findAll()).thenReturn(전체_노선_목록);
    }

    @Test
    void findPath() {
        // given
        when(pathFinder.find(전체_노선_목록, 강남역, 남부터미널역)).thenReturn(new PathResponse(getStationResponses(강남역, 교대역, 남부터미널역), 12));

        // when
        PathResponse path = pathService.findPath(1L, 2L);

        // then
        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(12),
                () -> assertThat(path.getStations()).extracting(StationResponse::getName)
                        .containsExactly("강남역", "교대역", "남부터미널역")
        );

    }

    private List<StationResponse> getStationResponses(Station... stations) {
        return Arrays.stream(stations)
                .map(station -> new StationResponse(station))
                .collect(Collectors.toList());
    }
}
