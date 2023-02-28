package nextstep.subway.unit;

import nextstep.subway.applicaion.DijkstraPathFinder;
import nextstep.subway.domain.Line;
import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DijkstraPathFinderTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private List<Line> lines;
    private PathFinder pathFinder;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재역
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line(1L, "2호선", "green");
        이호선.addSection(교대역, 강남역, 10);

        신분당선 = new Line(2L, "신분당선", "red");
        신분당선.addSection(강남역, 양재역, 10);

        삼호선 = new Line(3L, "3호선", "orange");
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
        lines = List.of(이호선, 신분당선, 삼호선);
        pathFinder = new DijkstraPathFinder(new WeightedMultigraph<>(DefaultWeightedEdge.class));
    }

    @DisplayName("출발역과 도착역을 설정하면 최단 경로를 조회할 수 있다.")
    @Test
    void findPath() {
        // when
        SubwayPath subwayPath = pathFinder.findPath(lines, 교대역, 양재역);

        // then
        assertAll(
                () -> assertThat(subwayPath.getStations()).containsExactly(교대역, 남부터미널역, 양재역),
                () -> assertThat(subwayPath.totalDistance()).isEqualTo(5)
        );
    }

    @DisplayName("출발역과 도착역이 같으면 경로 조회를 할 수 없다.")
    @Test
    void findPathSourceAndTargetEqualException() {
        // when & then
        assertThatThrownBy(() -> pathFinder.findPath(lines, 교대역, 교대역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 같은 경우 경로 조회를 할 수 없습니다.");
    }

    @DisplayName("출발역과 도착역이 연결된 노선이 없으면 경로 조회를 할 수 없다.")
    @Test
    void findPathSourceAndTargetNotInLineException() {
        // given
        Station 선릉역 = new Station(5L, "선릉역");
        Station 선정릉역 = new Station(6L, "선정릉역");
        Line 수인분당선 = new Line(4L, "수인분당선", "green");
        수인분당선.addSection(선릉역, 선정릉역, 2);

        // when & then
        assertThatThrownBy(() -> pathFinder.findPath(lines, 교대역, 선릉역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("graph must contain the sink vertex");
    }
}
