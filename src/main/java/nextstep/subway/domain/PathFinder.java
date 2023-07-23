package nextstep.subway.domain;

import nextstep.subway.domain.vo.Path;
import nextstep.subway.repository.LineRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PathFinder {

    private final LineRepository lineRepository;

    public PathFinder(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Path getShortestPath(Station sourceStation, Station targetStation) {
        if (Objects.equals(sourceStation, targetStation)) {
            throw new IllegalArgumentException();
        }

        GraphPath<Station, DefaultWeightedEdge> graphPath = initializePath(sourceStation, targetStation);

        List<Station> stations = graphPath.getVertexList();
        long distance = (long) graphPath.getWeight();
        return new Path(stations, distance);
    }

    private GraphPath<Station, DefaultWeightedEdge> initializePath(Station sourceStation, Station targetStation) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        List<Line> lines = lineRepository.findAll();

        addStationVertexes(graph, lines);
        addSectionEdges(graph, lines);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(sourceStation, targetStation);
    }

    private void addStationVertexes(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        lines.stream()
                .map(Line::getStations)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
                .forEach(graph::addVertex);
    }

    private void addSectionEdges(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}
