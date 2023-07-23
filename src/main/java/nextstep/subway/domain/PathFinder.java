package nextstep.subway.domain;

import nextstep.subway.domain.vo.Path;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class PathFinder {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathFinder(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Path getShortestPath(Station sourceStation, Station targetStation) {
        return initializePath(sourceStation, targetStation)
                .map(path -> {
                    List<Station> stations = path.getVertexList();
                    long distance = (long) path.getWeight();
                    return new Path(stations, distance);
                })
                .orElseThrow(IllegalArgumentException::new);
    }

    private Optional<GraphPath<Station, DefaultWeightedEdge>> initializePath(Station sourceStation, Station targetStation) {
        if (Objects.equals(sourceStation, targetStation)) {
            return Optional.empty();
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        addStationVertexes(graph);
        addSectionEdges(graph);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return Optional.ofNullable(dijkstraShortestPath.getPath(sourceStation, targetStation));
    }

    private void addStationVertexes(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        stationRepository.findAll().forEach(graph::addVertex);
    }

    private void addSectionEdges(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lineRepository.findAll().stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}
