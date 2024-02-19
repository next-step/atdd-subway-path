package nextstep.subway.strategy;

import nextstep.subway.domain.entity.Path;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.exception.ApplicationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static nextstep.subway.exception.ExceptionMessage.*;

public class DijkstraStrategy implements PathStrategy {

    private DijkstraShortestPath dijkstraShortestPath;

    public DijkstraStrategy(List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        createGraph(graph, sections);
        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    @Override
    public Path findShortestPath(Station source, Station target) {
        GraphPath graphPath;
        try {
            graphPath = dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(NO_EXISTS_STATION_EXCEPTION.getMessage());
        }

        // 경로가 없으면 예외처리
        if (ObjectUtils.isEmpty(graphPath)) {
            throw new ApplicationException(NOT_CONNECTED_EXCEPTION.getMessage());
        }

        return new Path(this, graphPath.getVertexList(), (int) graphPath.getWeight());
    }

    // graph 에 vertax, edge 추가
    private void createGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation= section.getDownStation();

            graph.addVertex(upStation);
            graph.addVertex(downStation);
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        }
    }
}
