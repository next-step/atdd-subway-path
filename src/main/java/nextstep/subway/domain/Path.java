package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class Path {

    private final Station source;
    private final Station target;
    private final List<Line> lines;

    private List<Station> stations;
    private int distance;


    public Path(Station source, Station target, List<Line> lines) {
        this.source = source;
        this.target = target;
        this.lines = lines;
    }

    public void findShortestPath() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Line line : lines) {
            addVertex(graph, line);
            addEdge(graph, line);
        }

        GraphPath path = new DijkstraShortestPath(graph).getPath(source, target);

        stations = path.getVertexList();
        distance = (int) path.getWeight();
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    private void addEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Line line) {
        for (Section section : line.getSections()) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

    private void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Line line) {
        for (Station station : line.getStations()) {
            graph.addVertex(station);
        }
    }

}
