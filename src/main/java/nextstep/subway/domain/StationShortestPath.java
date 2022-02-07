package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class StationShortestPath {

    private final DijkstraShortestPath dijkstraShortestPath;

    public static StationShortestPath of(List<Line> lines) {
        final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(graph::addVertex);
        lines.stream()
                .flatMap(it -> it.getSections().stream())
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getUpStation(), it.getDownStation()), it.getDistance()));
        return new StationShortestPath(new DijkstraShortestPath(graph));
    }

    private StationShortestPath(final DijkstraShortestPath dijkstraShortestPath) {
        this.dijkstraShortestPath = dijkstraShortestPath;
    }

    public List<Station> getPath(final Station sourceStation, final Station targetStation) {
        return dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
    }

    public int getPathWeight(final Station sourceStation, final Station targetStation) {
        return (int) dijkstraShortestPath.getPathWeight(sourceStation, targetStation);
    }
}
