package nextstep.subway.path;

import nextstep.subway.line.Section;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class Route {

    private WeightedMultigraph<Station, DefaultWeightedEdge> route;
    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public Route() {
        route = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        dijkstraShortestPath = new DijkstraShortestPath<>(route);
    }

    public void initGraph(List<Section> sections) {
        route = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        dijkstraShortestPath = new DijkstraShortestPath<>(route);
        initVertex(sections);
        initEdgeAndWeight(sections);
    }

    private void initVertex(List<Section> sections) {
        for (Station station : mapToStations(sections)) {
            route.addVertex(station);
        }
    }

    private List<Station> mapToStations(List<Section> sections) {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    private void initEdgeAndWeight(List<Section> sections) {
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            Long distance = section.getDistance();
            route.setEdgeWeight(route.addEdge(upStation, downStation), distance);
        }
    }

    public List<Station> findShortestPath(Station sourceStation, Station targetStation) {
        validate(sourceStation, targetStation);
        return getPath(sourceStation, targetStation).getVertexList();
    }

    public int findShortestDistance(Station sourceStation, Station targetStation) {
        validate(sourceStation, targetStation);
        return (int) getPath(sourceStation, targetStation).getWeight();
    }

    private GraphPath<Station, DefaultWeightedEdge> getPath(Station sourceStation, Station targetStation) {
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        if (path == null) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다. sourceStationId: " + sourceStation.getId() + ", targetStationId: " + targetStation.getId());
        }
        return path;
    }

    private void validate(Station sourceStation, Station targetStation) {
        validateSameStation(sourceStation, targetStation);
        validateExistStation(sourceStation, targetStation);
    }

    private void validateSameStation(Station sourceStation, Station targetStation) {
        if (sourceStation.isSameStation(targetStation)) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다. stationId: " + sourceStation.getId());
        }
    }

    private void validateExistStation(Station sourceStation, Station targetStation) {
        if (!route.containsVertex(sourceStation)) {
            throw new IllegalArgumentException("출발역이 존재하지 않습니다. stationId: " + sourceStation.getId());
        }
        if (!route.containsVertex(targetStation)) {
            throw new IllegalArgumentException("도착역이 존재하지 않습니다. stationId: " + targetStation.getId());
        }
    }
}
