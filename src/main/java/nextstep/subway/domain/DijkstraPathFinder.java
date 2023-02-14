package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.exception.CanNotFindShortestPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class DijkstraPathFinder implements PathFinder {

    @Override
    public Path searchShortestPath(PathRequest pathRequest, List<Section> sections) {
        if (pathRequest.isSourceAndTargetSame()) {
            throw new CanNotFindShortestPathException("출발역과 도착역은 같을수 없습니다.");
        }

        GraphPath graphPath = createGraphPath(pathRequest, createWeightedMultigraph(sections));

        return Path.of(
                graphPath.getVertexList(),
                (int) graphPath.getWeight());
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createWeightedMultigraph(List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertex(sections, graph);
        addEdgeWight(sections, graph);
        return graph;
    }

    private void addEdgeWight(List<Section> sections, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.stream()
                .forEach(s -> graph.setEdgeWeight(graph.addEdge(s.getUpStation(), s.getDownStation()), s.getDistance()));
    }

    private void addVertex(List<Section> sections, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.stream()
                .flatMap(s -> List.of(s.getUpStation(), s.getDownStation()).stream())
                .distinct()
                .collect(Collectors.toList())
                .forEach(graph::addVertex);
    }

    private GraphPath createGraphPath(PathRequest pathRequest, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(
                pathRequest.getSourceStation(),
                pathRequest.getTargetStation());
    }
}
