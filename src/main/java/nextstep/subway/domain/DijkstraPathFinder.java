package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.exception.CanNotFindShortestPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class DijkstraPathFinder implements PathFinder {

    @Override
    public Path searchShortestPath(PathRequest pathRequest, List<Section> sections) {
        List<Station> stations = getStations(sections);
        Station sourceStation = pathRequest.getSourceStation();
        Station targetStation = pathRequest.getTargetStation();

        if (pathRequest.isSourceAndTargetSame()) {
            throw new CanNotFindShortestPathException("출발역과 도착역은 같을수 없습니다.");
        }

        if (!stations.contains(sourceStation) && !stations.contains(targetStation)) {
            throw new CanNotFindShortestPathException("출발역과 도착역은 필수값 입니다.");
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> weightedMultigraph = createWeightedMultigraph(sections, stations);
        GraphPath graphPath = createGraphPath(weightedMultigraph, sourceStation, targetStation);

        return Path.of(
                graphPath.getVertexList(),
                (int) graphPath.getWeight());
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createWeightedMultigraph(List<Section> sections, List<Station> stations) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertex(stations, graph);
        addEdgeWight(sections, graph);
        return graph;
    }

    private void addEdgeWight(List<Section> sections, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.stream()
                .forEach(s -> graph.setEdgeWeight(graph.addEdge(s.getUpStation(), s.getDownStation()), s.getDistance()));
    }

    private void addVertex(List<Station> stations, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        stations.forEach(graph::addVertex);
    }

    private List<Station> getStations(List<Section> sections) {
        return sections.stream()
                .flatMap(s -> List.of(s.getUpStation(), s.getDownStation()).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private GraphPath createGraphPath(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station sourceStation, Station targetStation) {
        try {
            DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
            return dijkstraShortestPath.getPath(sourceStation, targetStation);
        } catch (IllegalArgumentException e) {
            throw new CanNotFindShortestPathException("출발역과 도착역은 연결되어 있어야 합니다.", e);
        }

    }
}
