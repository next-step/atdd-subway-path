package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.ShortPathSameStationException;
import nextstep.subway.exception.StationNotExistException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.*;

public class PathFinder {

    private List<Line> values;

    public PathFinder(List<Line> values) {
        this.values = values;
    }

    public ShortPath findShortPath(Station startStation, Station endStation) {
        if (isSameStation(startStation, endStation)) {
            throw new ShortPathSameStationException();
        }
        if (isNotExistStation(startStation, endStation)) {
            throw new StationNotExistException();
        }
        return getShortPath(startStation, endStation);
    }

    private boolean isSameStation(Station startStation, Station endStation) {
        return startStation.equals(endStation);
    }

    private boolean isNotExistStation(Station startStation, Station endStation) {
        List<Station> stations = getStations();
        return !stations.contains(startStation) || !stations.contains(endStation);
    }

    private ShortPath getShortPath(Station startStation, Station endStation) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        setGraphVertex(graph);
        setGraphEdgeWeight(graph);
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath graphPath = dijkstraShortestPath.getPath(startStation, endStation);
        return new ShortPath(graphPath.getVertexList(), graphPath.getWeight());
    }

    private void setGraphVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        for (Station station : getStations()) {
            graph.addVertex(station);
        }
    }

    private void setGraphEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        for (Section section : getSections()) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

    private List<Station> getStations() {
        Set<Station> stations = new HashSet<>();
        for (Line line : values) {
            stations.addAll(line.getStations());
        }
        return new ArrayList<>(stations);
    }

    private List<Section> getSections() {
        List<Section> sections = new ArrayList<>();
        for (Line line : values) {
            sections.addAll(line.getSections());
        }
        return sections;
    }

}
