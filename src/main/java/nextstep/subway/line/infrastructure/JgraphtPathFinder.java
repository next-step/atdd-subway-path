package nextstep.subway.line.infrastructure;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.common.domain.exception.ErrorMessage;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.infrastructure.dto.StationPath;
import nextstep.subway.station.domain.Station;

public class JgraphtPathFinder implements PathFinder {
    @Override
    public StationPath findShortestPaths(List<Section> sections, Station source, Station target) {
        verifySameSourceTarget(source, target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = graph(sections);
        ShortestPathAlgorithm<Station, DefaultWeightedEdge> algorithm = new DijkstraShortestPath<>(graph);

        return toStationPath(algorithm.getPath(source, target));
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
        List<Station> stations = Stream.concat(upStationStream, downStationStream)
                                       .distinct()
                                       .collect(Collectors.toList());
        stations.forEach(graph::addVertex);
    }

    private void setAllEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section eachSection : sections) {
            DefaultWeightedEdge edge = graph.addEdge(eachSection.getUpStation(), eachSection.getDownStation());
            graph.setEdgeWeight(edge, eachSection.getDistance().getValue());
        }
    }

    private StationPath toStationPath(GraphPath<Station, DefaultWeightedEdge> graphPath) {
        return new StationPath(graphPath.getVertexList(), new Distance((int) graphPath.getWeight()));
    }
}
