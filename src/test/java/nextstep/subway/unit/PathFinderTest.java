package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PathFinderTest {

    private Station 고속터미널역;
    private Station 교대역;
    private Station 강남역;
    private Station 신논현역;
    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;
    private Line 구호선;

    /**
     * 고속터미널역 --- *9호선*(4) --- 신논현역
     * |                            |
     * *3호선(3)*                 *신분당선*(1)
     * |                            |
     * 교대역   --- *2호선*(3) ---   강남역
     */
    @BeforeEach
    public void setUp() {
        고속터미널역 = new Station("고속터미널역");
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        신논현역 = new Station("신논현역");

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 신논현역, 강남역, 1));
        이호선 = new Line("이호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 3));
        삼호선 = new Line("삼호선", "orange");
        삼호선.addSection(new Section(삼호선, 고속터미널역, 교대역, 3));
        구호선 = new Line("구호선", "gold");
        구호선.addSection(new Section(구호선, 고속터미널역, 신논현역, 4));
    }

    @Test
    void 노선_정보를_받아_주어진_역_간_최단_경로를_계산한다() {
        // given
        PathFinder pathFinder = new PathFinder(List.of(신분당선, 이호선, 삼호선, 구호선));

        // when
        GraphPath<Station, DefaultWeightedEdge> shortestPath = pathFinder.getShortestPath(고속터미널역, 강남역);

        // then
        assertAll(
                () -> assertThat(shortestPath.getVertexList()).containsExactly(고속터미널역, 신논현역, 강남역),
                () -> assertThat(shortestPath.getWeight()).isEqualTo(5)
        );
    }

    @Test
    void 출발역과_도착역이_같은_경우_예외를_일으킨다() {
        // given
        PathFinder pathFinder = new PathFinder(List.of(신분당선, 이호선, 삼호선, 구호선));

        // when & then
        assertThatIllegalArgumentException().isThrownBy(() ->
                pathFinder.getShortestPath(고속터미널역, 고속터미널역)
        );
    }

    @Test
    void 출발역과_도착역이_연결이_되어_있지_않은_경우_예외를_일으킨다() {
        // given
        PathFinder pathFinder = new PathFinder(List.of(신분당선, 삼호선));

        // when & then
        assertThatIllegalArgumentException().isThrownBy(() ->
                pathFinder.getShortestPath(고속터미널역, 신논현역)
        );
    }

    @Test
    void 존재하지_않는_출발역이나_도착역을_조회할_경우_예외를_일으킨다() {
        // given
        PathFinder pathFinder = new PathFinder(List.of(신분당선, 구호선));

        // when & then
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() ->
                        pathFinder.getShortestPath(고속터미널역, 교대역)
                ),
                () -> assertThatIllegalArgumentException().isThrownBy(() ->
                        pathFinder.getShortestPath(교대역, 고속터미널역)
                )
        );
    }
}
