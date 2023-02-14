package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathRequest;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class DijkstraPathFinder implements PathFinder {

    @Override
    public Path searchShortestPath(PathRequest pathRequest, List<Section> sections) {

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        sections.stream()
                .flatMap(s -> List.of(s.getUpStation(), s.getDownStation()).stream())
                .distinct()
                .collect(Collectors.toList())
                .forEach(s -> graph.addVertex(s));

        sections.stream()
                .forEach(s -> graph.setEdgeWeight(graph.addEdge(s.getUpStation(), s.getDownStation()), s.getDistance()));

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath graphPath = dijkstraShortestPath.getPath(new Station(pathRequest.getSource()), new Station(pathRequest.getTarget()));

        List<Station> shortestPath = graphPath.getVertexList();
        int distance = (int) graphPath.getWeight();

        return Path.of(shortestPath, distance);
    }
}
