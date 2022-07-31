package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SubwayGraph implements CommonGraph {
    List<Line> lines;
    WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath;

    public SubwayGraph(List<Line> lines) {
        this.lines = lines;
        this.graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        initGraph();
        this.shortestPath = new DijkstraShortestPath<>(graph);
    }

    private void initGraph() {
        List<Station> stations = getSubwayAllStations();
        makeVertexes(stations);

        List<Section> sections = getSubwayAllSections();
        makeEdgeWeights(sections);
    }

    private List<Station> getSubwayAllStations() {
        return lines.stream()
                .map(Line::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private void makeVertexes(List<Station> stations) {
        stations.forEach(station -> graph.addVertex(station));
    }

    private List<Section> getSubwayAllSections() {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void makeEdgeWeights(List<Section> sections) {
        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation())
                    , section.getDistance());
        }

    }

    public List<Station> getShortestPath(Station source, Station target) {
        checkStationStatus(lines, source, target);
        GraphPath<Station, DefaultWeightedEdge> path = shortestPath.getPath(source, target);
        if (path == null) {
            throw new IllegalArgumentException("STATIONS_ARE_NOT_LINKED");
        }
        return path.getVertexList();
    }

    public int getShortestDistance(Station source, Station target) {
        checkStationStatus(lines, source, target);

        GraphPath<Station, DefaultWeightedEdge> result = shortestPath.getPath(source, target);
        if (result == null) {
            throw new IllegalArgumentException("STATIONS_ARE_NOT_LINKED");
        }

        int distance = (int) result.getWeight();
        return distance;
    }

    private void checkStationStatus(List<Line> lines, Station source, Station target) {
        if (isSameStation(source, target)) {
            throw new IllegalArgumentException("IS_SAME_STATION");
        }
        if (!haveStations(lines, source, target)) {
            throw new IllegalArgumentException("HAVE_NOT_STATION");
        }
    }

    private boolean isSameStation(Station source, Station target) {
        return source.equals(target);
    }

    private boolean haveStations(List<Line> lines, Station source, Station target) {
        boolean haveSource = false;
        boolean haveTarget = false;
        for (Line line : lines) {
            List<Station> stations = line.getStations();
            if (stations.contains(source)) {
                haveSource = true;
            }
            if (stations.contains(target)) {
                haveTarget = true;
            }
        }

        if (haveSource && haveTarget) {
            return true;
        }
        return false;

    }


}
