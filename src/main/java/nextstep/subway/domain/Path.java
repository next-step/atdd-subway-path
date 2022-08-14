package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Set;

public class Path {

    public DijkstraShortestPath pathGeneratior;

    public Path(Set<Station> stations, List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);

        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));

        this.pathGeneratior = new DijkstraShortestPath(graph);
    }

    public List<Station> getShortestPath(Station start, Station end) {
        GraphPath<Station, Station> shortestPath = pathGeneratior.getPath(start, end);

        if (shortestPath == null) {
            throw new IllegalArgumentException("경로가 존재하지 않습니다.");
        }

        return shortestPath.getVertexList();
    }

    public int getShortestWeight(Station start, Station end) {
        return (int) pathGeneratior.getPathWeight(start, end);
    }
}
