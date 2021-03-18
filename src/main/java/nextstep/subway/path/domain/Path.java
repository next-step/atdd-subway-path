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

    private List<Section> sections;
    private Set<Station> stations;
    private GraphPath<Station, DefaultWeightedEdge> graphPath;

    public Path() {
    }

    public Path(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> findShortestPath(Station source, Station target) {
        validateSourceAndTargetStation(source, target);

        graphPath = getGraphPath(source, target);

        return graphPath.getVertexList();
    }

    private void validateSourceAndTargetStation(Station source, Station target) {
        if (source.equals(target)) {
            throw new SameStationPathSearchException();
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> getGraphPath(Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> lineSectionsGraph = initLineSectionsGraph();
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(lineSectionsGraph);
        validateConnectPathStation(source, target);
        return dijkstraShortestPath.getPath(source, target);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> initLineSectionsGraph() {
        WeightedMultigraph<Station, DefaultWeightedEdge> lineSectionsGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        Set<Station> stations = getDistinctStations();

        addVertex(stations, lineSectionsGraph);
        addEdge(lineSectionsGraph);

        return lineSectionsGraph;
    }

    private Set<Station> getDistinctStations() {
        stations = new HashSet<>();

        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }

    private void addVertex(Set<Station> stations, WeightedMultigraph<Station, DefaultWeightedEdge> lineSectionsGraph) {
        for (Station station : stations) {
            lineSectionsGraph.addVertex(station);
        }
    }

    private void addEdge(WeightedMultigraph<Station, DefaultWeightedEdge> lineSectionsGraph) {
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            int distance = section.getDistance();

            DefaultWeightedEdge edge = lineSectionsGraph.addEdge(upStation, downStation);
            lineSectionsGraph.setEdgeWeight(edge, distance);
        }
    }

    private void validateConnectPathStation(Station source, Station target) {
        if (!stations.contains(source) || !stations.contains(target)) {
            throw new DoesNotConnectedPathException();
        }
    }

    public int getTotalDistance() {
        return (int) graphPath.getWeight();
    }
}
