package nextstep.subway.domain.path;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.exception.domain.PathFinderException;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
public class PathFinder {

    private List<Section> sections;
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Section> sections) {
        this.sections = sections;
        initPath();
    }

    private void initPath() {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initVertex(graph);
        initEdge(graph);
    }

    private void initVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.stream()
                .map(section -> Set.of(section.getUpStation(), section.getDownStation()))
                .flatMap(Collection::stream)
                .forEach(graph::addVertex);
    }

    private void initEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        for (Section section : sections) {
            DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
            graph.setEdgeWeight(edge, section.getDistance());
        }
    }

    public GraphPath findPath(Station sourceStation, Station targetStation) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        hasStation(sourceStation, targetStation);
        GraphPath shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        if (shortestPath == null) throw new PathFinderException("조회되지 않는 경로입니다.");
        return shortestPath;
    }

    private void hasStation(Station sourceStation, Station targetStation) {
        if (!graph.containsVertex(sourceStation)) {
            throw new PathFinderException("요청한 출발역은 연결되지 않은 역입니다.");
        } else if (!graph.containsVertex(targetStation)) {
            throw new PathFinderException("요청한 종점역은 연결되지 않은 역입니다.");
        }
    }
}