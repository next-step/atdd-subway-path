package nextstep.subway.domain;

import nextstep.subway.exception.PathFinderException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathFinder {

    private List<Station> stations;

    private int distance;

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public PathFinder(List<Line> lines, Station sourceStation, Station targetStation) {
        validateSourceAndTarget(sourceStation, targetStation);
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = drawGraph(lines);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = getShortestPath(graph, sourceStation, targetStation);
        this.stations = shortestPath.getVertexList();
        this.distance = (int) shortestPath.getWeight();
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> drawGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertices(graph, lines);
        setEdgeWeights(graph, lines);
        return graph;
    }

    private void addVertices(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        Set<Station> stations = new HashSet<>();
        for (Line line : lines) {
            stations.addAll(line.getStations());
        }
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private void setEdgeWeights(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        List<Section> sections = new ArrayList<>();
        for (Line line : lines) {
            sections.addAll(line.getSections());
        }
        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> getShortestPath(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station sourceStation, Station targetStation) {
        try {
            return new DijkstraShortestPath<>(graph).getPath(sourceStation, targetStation);
        } catch (IllegalArgumentException e) {
            throw new PathFinderException("출발역과 도착역이 연결되어 있지 않거나 존재하지 않은 출발역이나 도착역 입니다.");
        }
    }

    private void validateSourceAndTarget(Station sourceStation, Station targetStation) {
        if (sourceStation == targetStation) {
            throw new PathFinderException("출발역과 도착역은 같을 수 없습니다.");
        }
    }
}
