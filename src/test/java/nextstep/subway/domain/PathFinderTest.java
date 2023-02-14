package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 천호역;
    private Station 광나루역;
    private Station 런던역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Line 오호선;
    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");
        광나루역 = new Station("광나루역");
        천호역 = new Station("천호역");
        런던역 = new Station("런던역");


        이호선 = new Line("2호선", "green");
        신분당선 = new Line("신분당선", "purple");
        삼호선 = new Line("3호선", "red");
        오호선 = new Line("5호선", "orange");

        이호선.addSection(교대역, 강남역, 10);
        신분당선.addSection(강남역, 양재역, 10);
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
        오호선.addSection(광나루역, 천호역, 8);

        pathFinder = new PathFinder();
        pathFinder.init(List.of(이호선, 신분당선, 삼호선, 오호선));
    }

    @Test
    public void shortestPath() {
        // when
        GraphPath<Station, DefaultWeightedEdge> shortestPath = pathFinder.getShortestPath(교대역, 양재역);

        // then
        assertThat(shortestPath.getVertexList()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(shortestPath.getWeight()).isEqualTo(5);
    }

    @Test
    public void sameTargetAndSource() {
        // when & then
        assertThatThrownBy(() -> pathFinder.getShortestPath(교대역, 교대역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(PathErrorMessage.FIND_PATH_SAME_TARGET_AND_SOURCE.getMessage());
    }

    @Test
    public void notConnectedPath() {
        // when & then
        assertThatThrownBy(() -> pathFinder.getShortestPath(교대역, 천호역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(PathErrorMessage.FIND_PATH_NOT_CONNECTED.getMessage());
    }

    @Test
    public void nonExistentSource() {
        // when & then
        assertThatThrownBy(() -> pathFinder.getShortestPath(런던역, 교대역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(PathErrorMessage.FIND_PATH_STATION_NOT_EXIST.getMessage());

    }

    @Test
    public void nonExistentTarget() {
        // when & then
        assertThatThrownBy(() -> pathFinder.getShortestPath(교대역, 런던역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(PathErrorMessage.FIND_PATH_STATION_NOT_EXIST.getMessage());

    }
}