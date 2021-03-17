package nextstep.subway.path.domain;

import nextstep.subway.path.exception.SourceEqualsWithTargetException;
import nextstep.subway.path.exception.StationsNotConnectedException;
import nextstep.subway.station.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {
    private PathFinder pathFinder;

    @DisplayName("최단 경로를 조회한다.")
    @Test
    void getShortestPathTest() {
        // given
        WeightedMultigraph<Station, Integer> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 교대역 = new Station("교대역");

        graph.setEdgeWeight(graph.addEdge(강남역, 역삼역), 3);
        graph.setEdgeWeight(graph.addEdge(역삼역, 교대역), 7);
        graph.setEdgeWeight(graph.addEdge(강남역, 교대역), 100);

        pathFinder = new PathFinder(graph);

        // when
        List<Station> stations = pathFinder.getShortestPath(강남역, 교대역);

        // then
        assertThat(stations).isEqualTo(Arrays.asList(강남역, 역삼역, 교대역));
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void getShortestPathTest_WhenSourceEqualsWithTarget() {
        // given
        WeightedMultigraph<Station, Integer> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 교대역 = new Station("교대역");

        graph.setEdgeWeight(graph.addEdge(강남역, 역삼역), 3);
        graph.setEdgeWeight(graph.addEdge(역삼역, 교대역), 7);
        graph.setEdgeWeight(graph.addEdge(강남역, 교대역), 100);

        pathFinder = new PathFinder(graph);

        // when
        // then
        assertThatThrownBy(() -> {
            pathFinder.getShortestPath(강남역, 강남역);
        }).isInstanceOf(SourceEqualsWithTargetException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void getShortestPathTest_SourceAndTargetNotConnected() {
        // given
        WeightedMultigraph<Station, Integer> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 교대역 = new Station("교대역");
        Station 사당역 = new Station("사당역");

        graph.setEdgeWeight(graph.addEdge(강남역, 역삼역), 3);
        graph.setEdgeWeight(graph.addEdge(역삼역, 교대역), 7);
        graph.setEdgeWeight(graph.addEdge(강남역, 교대역), 100);

        pathFinder = new PathFinder(graph);

        // when
        // then
        assertThatThrownBy( () -> {
            pathFinder.getShortestPath(강남역, 사당역);
        }).isInstanceOf(StationsNotConnectedException.class);
    }

    @DisplayName("존재하지 않는 출발역이나 도착역을 조회 할 경우")
    @Test
    void getShortestPathTest_WhenStationNotExists() {
        // given
        WeightedMultigraph<Station, Integer> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 교대역 = new Station("교대역");

        graph.setEdgeWeight(graph.addEdge(강남역, 역삼역), 3);
        graph.setEdgeWeight(graph.addEdge(역삼역, 교대역), 7);
        graph.setEdgeWeight(graph.addEdge(강남역, 교대역), 100);

        pathFinder = new PathFinder(graph);

        // when
        // then
        assertThatThrownBy( () -> {
            pathFinder.getShortestPath(강남역, new Station("남부터미널역"));
        }).isInstanceOf(StationsNotConnectedException.class);
    }
}
