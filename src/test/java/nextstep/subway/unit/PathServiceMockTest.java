package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.ShortestRoute;
import nextstep.subway.domain.Station;
import nextstep.subway.infrastructure.PathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static nextstep.subway.utils.LineTestFixtures.*;
import static nextstep.subway.utils.StationTestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    PathService pathService;

    @Mock
    PathFinder pathFinder;

    @Mock
    StationService stationService;

    @Mock
    LineService lineService;

    private Station 교대역;
    private Station 강남역;
    private Station 남부터미널역;
    private Station 양재역;

    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;

    /**
     * 교대역      ---     이호선      ---      강남역
     * |                                        |
     * 삼호선                                 신분당선
     * |                                        |
     * 남부터미널역  ---    삼호선      ---      양재역
     *
     */

    @BeforeEach
    void setup() {
        교대역 = 지하철역_생성_WITH_ID("교대역", 1L);
        강남역 = 지하철역_생성_WITH_ID("강남역", 2L);
        남부터미널역 = 지하철역_생성_WITH_ID("남부터미널역", 3L);
        양재역 = 지하철역_생성_WITH_ID("양재역", 4L);

        이호선 = 노선_생성("이호선", "green", 교대역, 강남역, 10);
        신분당선 = 노선_생성("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 노선_생성("삼호선", "orange", 교대역, 남부터미널역, 2);

        삼호선.addSection(남부터미널역, 양재역, 3);
        pathService = new PathService(lineService, stationService);
    }

    @DisplayName("출발역에서 도착역까지의 지하철역 경로 조회")
    @Test
    void findRoutes() {

        // given
        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(4L)).thenReturn(양재역);

        when(lineService.findAll()).thenReturn(Arrays.asList(이호선, 삼호선, 신분당선));

        // when
        ShortestRoute results = pathService.findRoutes(교대역.getId(), 양재역.getId());

        // then
        assertThat(results.routes()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(results.distance()).isEqualTo(5L);
    }

    @DisplayName("출발역과 도착역이 같은 경로를 조회")
    @Test
    void InternalErrorFindRoutesIfSourceAndTargetEquals() {

        // given
        when(stationService.findById(1L)).thenReturn(교대역);

        when(lineService.findAll()).thenReturn(Arrays.asList(이호선, 삼호선, 신분당선));

        // then
        assertThatThrownBy(() -> pathService.findRoutes(교대역.getId(), 교대역.getId()))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("이어지지 않은 경로를 조회")
    @Test
    void InternalErrorFindRoutesIfNotConnectedStations() {

        // given
        final Station 구로역 = 지하철역_생성_WITH_ID("구로역", 5L);
        final Station 신도림역 = 지하철역_생성_WITH_ID("신도림역", 6L);

        final Line 일호선 = 노선_생성_WITH_ID("일호선", "blue", 구로역, 신도림역, 5, 4L);

        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(5L)).thenReturn(구로역);
        when(lineService.findAll()).thenReturn(Arrays.asList(이호선, 삼호선, 신분당선, 일호선));

        // then
        assertThatThrownBy(() -> pathService.findRoutes(교대역.getId(), 구로역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않은 출발역을 기점으로 경로를 조회")
    @Test
    void InternalErrorFindRoutesIfNotExistSource() {

        // given
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 5L);
        when(stationService.findById(5L)).thenReturn(구로디지털단지역);
        when(stationService.findById(4L)).thenReturn(양재역);

        when(lineService.findAll()).thenReturn(Arrays.asList(이호선, 삼호선, 신분당선));

        // then
        assertThatThrownBy(() -> pathService.findRoutes(구로디지털단지역.getId(), 양재역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않은 도착역을 기점으로 경로를 조회")
    @Test
    void InternalErrorFindRoutesIfNotExistTarget() {

        // given

        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 5L);
        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(5L)).thenReturn(구로디지털단지역);

        when(lineService.findAll()).thenReturn(Arrays.asList(이호선, 삼호선, 신분당선));

        // then
        assertThatThrownBy(() -> pathService.findRoutes(교대역.getId(), 구로디지털단지역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
