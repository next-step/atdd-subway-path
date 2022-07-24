package nextstep.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class PathGraphTest {

    private final Line 이호선 = new Line("2호선", "green");
    private final Line 신분당선 = new Line("신분당선", "red");
    private final Line 삼호선 = new Line("삼호선", "orange");
    private final Station 양재역 = new Station("양재역");
    private final Station 남부터미널역 = new Station("남부터미널역");
    private final Station 교대역 = new Station("교대역");
    private final Station 강남역 = new Station("강남역");

    private PathGraph pathGraph;

    @BeforeEach
    void setUp() {
        this.pathGraph = new PathGraph(List.of(
                new Section(이호선, 교대역, 양재역, 10),
                new Section(신분당선, 강남역, 양재역, 10),
                new Section(삼호선, 교대역, 남부터미널역, 2),
                new Section(삼호선, 남부터미널역, 양재역, 3)
        ));
    }

    @Test
    @DisplayName("최단거리 역을 반환한다.")
    void getShortestPathTest() {
        StationPath shortestPath = pathGraph.getShortestPath(교대역, 양재역);
        assertThat(shortestPath.getStations()).hasSize(3);
        assertThat(shortestPath.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(shortestPath.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("최단거리 역을 반환한다.")
    void getShortestPathFailTest() {
        assertThatIllegalArgumentException().isThrownBy(() -> pathGraph.getShortestPath(교대역, new Station("없는역")))
                .withMessage("경로를 찾을 수 없어요.");
    }

}