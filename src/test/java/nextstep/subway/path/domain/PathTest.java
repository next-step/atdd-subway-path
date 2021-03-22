package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.exception.SameSourceTargetException;
import nextstep.subway.path.exception.SourceTargetNotReachable;
import nextstep.subway.station.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static nextstep.subway.path.domain.PathHelper.*;

@DisplayName("최단 경로 탐색 테스트")
public class PathTest {
    WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    Path path;
    @BeforeEach
    void setUp(){
        Station station1 = 역_만들기(1L);
        Station station2 = 역_만들기(2L);
        Station station3 = 역_만들기(3L);
        Station station4 = 역_만들기(4L);

        Line line = new Line("1호선", "red", station1, station2, 20);
        line.addSection(station2, station3, 20);
        Line line2 = new Line("2호선", "green", station1, station4, 10);

        path = new Path();

        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");
        graph.addVertex("4");
        graph.setEdgeWeight(graph.addEdge("1", "2"), 2);
        graph.setEdgeWeight(graph.addEdge("2", "3"), 2);
        graph.setEdgeWeight(graph.addEdge("3", "4"), 2);
        graph.setEdgeWeight(graph.addEdge("1", "4"), 2);
    }

    @DisplayName("가장 짧은 경로 테스트")
    @Test
    void 가장_짧은_경로_반환(){
        //when
        List<String> pathList = path.getShortestPathList(graph, 1L, 4L);

        //then
        assertThat(pathList).containsExactlyElementsOf(Arrays.asList("1", "4"));
    }

    @DisplayName("가장 짧은 경로값 테스트")
    @Test
    void 가장_짧은_경로값_반환(){
        //when
        int length = path.getShortestPathLength(graph, 1L, 4L);

        //then
        assertThat(length).isEqualTo(2);
    }

    @DisplayName("시작, 끝점 같을 때")
    @Test
    void 시작_끝_같을_때(){
        //when
        //then
        assertThatThrownBy(() -> path.getGraphPath(graph, 1L, 1L))
                .isInstanceOf(SameSourceTargetException.class);
    }

    @DisplayName("서로 만날 수 없는 역일때")
    @Test
    void 서로_만날_수_없는_역일때(){
        //given
        graph.addVertex("5");
        graph.addVertex("6");
        graph.setEdgeWeight(graph.addEdge("5", "6"), 2);

        //when
        //then
        assertThatThrownBy(() -> path.getGraphPath(graph, 1L, 6L))
                .isInstanceOf(SourceTargetNotReachable.class);
    }
}
