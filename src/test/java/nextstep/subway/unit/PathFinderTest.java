package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.ShortestRoute;
import nextstep.subway.domain.Station;
import nextstep.subway.infrastructure.PathFinder;
import nextstep.subway.utils.StationTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nextstep.subway.utils.LineTestFixtures.노선_생성;
import static nextstep.subway.utils.LineTestFixtures.노선_생성_WITH_ID;
import static nextstep.subway.utils.StationTestFixtures.지하철역_생성_WITH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

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

        이호선 = 노선_생성_WITH_ID("이호선", "green", 교대역, 강남역, 10, 1L);
        신분당선 = 노선_생성_WITH_ID("신분당선", "red", 강남역, 양재역, 10, 2L);
        삼호선 = 노선_생성_WITH_ID("삼호선", "orange", 교대역, 남부터미널역, 2, 3L);

        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @DisplayName("출발역에서 도착역까지의 지하철역 경로 조회")
    @Test
    void findRoutes() {

        List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선);

        PathFinder pathFinder = new PathFinder(lines);
        ShortestRoute shortestRoute = pathFinder.findRoutes(교대역, 양재역);

        assertThat(shortestRoute.distance()).isEqualTo(5.0);
        assertThat(shortestRoute.routes()).containsExactly(교대역, 남부터미널역, 양재역);
    }


    @DisplayName("출발역과 도착역이 같은 경로를 조회")
    @Test
    void throwsExceptionFindRoutesIfSourceAndTargetEquals() {

        // given
        List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선);
        PathFinder pathFinder = new PathFinder(lines);

        // then
        assertThatThrownBy(() -> pathFinder.findRoutes(교대역, 교대역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이어지지 않은 경로를 조회")
    @Test
    void throwsExceptionFindRoutesIfNotConnectedRoute() {

        // given
        final Station 구로역 = 지하철역_생성_WITH_ID("구로역", 5L);
        final Station 신도림역 = 지하철역_생성_WITH_ID("신도림역", 6L);

        final Line 일호선 = 노선_생성_WITH_ID("일호선", "blue", 구로역, 신도림역, 5, 4L);
        List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선, 일호선);
        PathFinder pathFinder = new PathFinder(lines);

        // then
        assertThatThrownBy(() -> pathFinder.findRoutes(교대역, 구로역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않은 출발역을 기점으로 경로를 조회")
    @Test
    void throwsExceptionFindRoutesIfNotExistSource() {

        // given
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 5L);
        List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선);
        PathFinder pathFinder = new PathFinder(lines);

        // then
        assertThatThrownBy(() -> pathFinder.findRoutes(구로디지털단지역, 양재역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않은 출발역을 기점으로 경로를 조회")
    @Test
    void throwsExceptionFindRoutesIfNotExistTarget() {

        // given
        final Station 구로디지털단지역 = 지하철역_생성_WITH_ID("구로디지털단지역", 5L);
        List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선);
        PathFinder pathFinder = new PathFinder(lines);

        // then
        assertThatThrownBy(() -> pathFinder.findRoutes(교대역, 구로디지털단지역))
                .isInstanceOf(IllegalArgumentException.class);
    }


}
