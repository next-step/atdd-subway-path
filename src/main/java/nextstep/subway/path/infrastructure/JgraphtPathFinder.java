package nextstep.subway.path.infrastructure;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import nextstep.subway.common.domain.exception.ErrorMessage;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.StationPaths;
import nextstep.subway.station.domain.Station;

@Component
public class JgraphtPathFinder implements PathFinder {
    @Override
    public StationPaths findShortestPaths(List<Section> sections, Station source, Station target) {
        verifySameSourceTarget(source, target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = graph(sections);
        ShortestPathAlgorithm<Station, DefaultWeightedEdge> algorithm = new DijkstraShortestPath<>(graph);

        try {
            return toStationPath(algorithm.getPath(source, target));
        } catch(IllegalArgumentException e) {
            if ("graph must contain the sink vertex".equals(e.getMessage())) {
                throw new IllegalArgumentException(ErrorMessage.DISCONNECT_STATIONS.getMessage());
            }
            throw e;
        } catch(NullPointerException e) {
            throw new IllegalArgumentException(ErrorMessage.DISCONNECT_STATIONS.getMessage());
        }
    }

    private void verifySameSourceTarget(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(ErrorMessage.SAME_SOURCE_TARGET.getMessage());
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph(List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addAllVertex(graph, sections);
        setAllEdgeWeight(graph, sections);
        return graph;
    }

    private void addAllVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        Stream<Station> upStationStream = sections.stream().map(Section::getUpStation);
        Stream<Station> downStationStream = sections.stream().map(Section::getDownStation);
        Set<Station> stations = Stream.concat(upStationStream, downStationStream)
                                      .collect(Collectors.toSet());
        stations.forEach(graph::addVertex);
    }

    private void setAllEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section eachSection : sections) {
            DefaultWeightedEdge edge = graph.addEdge(eachSection.getUpStation(), eachSection.getDownStation());
            graph.setEdgeWeight(edge, eachSection.getDistance()
                                                 .getValue()
            );
        }
    }

    private StationPaths toStationPath(GraphPath<Station, DefaultWeightedEdge> graphPath) {
        return new StationPaths(
            graphPath.getVertexList(),
            new Distance((int) graphPath.getWeight())
        );
    }
}
