package nextstep.subway.unit.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.exception.NotConnectedException;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.SameStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.PathUnitTestHelper.SHORTEST_DISTANCE_YANGJAE_TO_YEOKSAM;
import static nextstep.subway.unit.PathUnitTestHelper.STATIONS_SIZE_YANGJAE_TO_YEOKSAM;
import static nextstep.subway.unit.PathUnitTestHelper.givens;
import static nextstep.subway.unit.PathUnitTestHelper.lines;
import static nextstep.subway.unit.PathUnitTestHelper.삼성역;
import static nextstep.subway.unit.PathUnitTestHelper.석촌역;
import static nextstep.subway.unit.PathUnitTestHelper.양재역;
import static nextstep.subway.unit.PathUnitTestHelper.역삼역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {

    private PathFinder pathFinder;

    /**
     * 교대역(1) --- *2호선* ---   강남역(2) --- *2호선* ---  역삼역(3)    석촌역(6)  --- *8호선* --- 단대오거리역(7)
     * |                            |
     * *3호선*                   *신분당선*
     * |                             |
     * 남부터미널역(4)  --- *3호선* ---   양재역(5)
     */
    @BeforeEach
    void setFixtures() {
        givens();
        pathFinder = PathFinder.of(lines);
    }

    @Test
    void 최단_경로_조회() {
        PathResponse response = pathFinder.findShortestPath(양재역, 역삼역);
        assertThat(response.getStations()).hasSize(STATIONS_SIZE_YANGJAE_TO_YEOKSAM);
        assertThat(response.getDistance()).isEqualTo(SHORTEST_DISTANCE_YANGJAE_TO_YEOKSAM);
    }

    @Test
    void 출발역과_도착역이_같으면_경로_조회가_실패_한다() {
        assertThatThrownBy(() -> pathFinder.findShortestPath(양재역, 양재역))
                .isInstanceOf(SameStationException.class);
    }

    @Test
    void 출발역과_도착역이_연결되어있지_않으면_경로_조회가_실패_한다() {
        assertThatThrownBy(() -> pathFinder.findShortestPath(양재역, 석촌역))
                .isInstanceOf(NotConnectedException.class);
    }

    @Test
    void 출발역_또는_도착역이_존재하지_않으면_경로_조회가_실패_한다() {
        assertThatThrownBy(() -> pathFinder.findShortestPath(양재역, 삼성역))
                .isInstanceOf(NotFoundStationException.class);
    }
}
