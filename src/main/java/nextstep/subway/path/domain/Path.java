package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.path.exception.DoesNotConnectedPathException;
import nextstep.subway.path.exception.SameStationPathSearchException;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Path {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> lineSectionsGraph;
    private final GraphPath<Station, DefaultWeightedEdge> graphPath;

    public Path(List<Section> sections, Station source, Station target) {
        lineSectionsGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        validateSourceAndTargetStation(source, target);
        graphPath = initShortestPathGraph(sections, source, target);
    }

    private void validateSourceAndTargetStation(Station source, Station target) {
        if (source.equals(target)) {
            throw new SameStationPathSearchException();
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> initShortestPathGraph(List<Section> sections, Station source, Station target) {
        Set<Station> stations = getDistinctStations(sections);

        addVertex(stations);
        addEdge(sections);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(lineSectionsGraph);
        validateConnectPathStation(stations, source, target);
        return dijkstraShortestPath.getPath(source, target);
    }

    private Set<Station> getDistinctStations(List<Section> sections) {
        Set<Station> stations = new HashSet<>();

        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }

    private void addVertex(Set<Station> stations) {
        for (Station station : stations) {
            lineSectionsGraph.addVertex(station);
        }
    }

    private void addEdge(List<Section> sections) {
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            int distance = section.getDistance();

            DefaultWeightedEdge edge = lineSectionsGraph.addEdge(upStation, downStation);
            lineSectionsGraph.setEdgeWeight(edge, distance);
        }
    }

    private void validateConnectPathStation(Set<Station> stations, Station source, Station target) {
        if (!stations.contains(source) || !stations.contains(target)) {
            throw new DoesNotConnectedPathException();
        }
    }

    public List<Station> findShortestPath() {
        return graphPath.getVertexList();
    }

    public int getTotalDistance() {
        return (int) graphPath.getWeight();
    }
}
