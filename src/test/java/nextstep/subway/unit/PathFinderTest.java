package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.infra.DijkstraShortestPathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.common.constants.ErrorConstant.SAME_STATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {

    PathFinder pathFinder;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;
    Station 죽전역;
    Station 보정역;

    Line 이호선;
    Line 신분당선;
    Line 삼호선;
    Line 수인분당선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* --- 양재역
     */
    @BeforeEach
    void setUp() {
        // given
        pathFinder = new DijkstraShortestPathFinder();

        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");
        죽전역 = new Station("죽전역");
        보정역 = new Station("보정역");

        이호선 = new Line("2호선", "green", new Section(교대역, 강남역, 10));
        신분당선 = new Line("신분당선", "red", new Section(강남역, 양재역, 10));
        삼호선 = new Line("3호선", "orange", new Section(교대역, 남부터미널역, 2));
        수인분당선 = new Line("수인분당선", "yellow", new Section(죽전역, 보정역, 2));

        삼호선.addSection(new Section(남부터미널역, 양재역, 3));
    }

    /**
     * 교대역    --- *2호선* ---   강남역
     */
    @Test
    @DisplayName("경로조회 실패-같은 출발역과 도착역")
    void findPath_sameSourceTarget() {
        // given
        pathFinder.init(List.of(이호선));

        // when
        // then
        assertThatThrownBy(() -> pathFinder.find(강남역, 강남역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SAME_STATION);
    }
}
