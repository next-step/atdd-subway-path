package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinder {
    public StationGraphPath getShortestPath(List<Section> sections, Station source, Station target){
        List<Station> stations = sections.stream()
                .flatMap(section -> Stream.of(
                        section.getUpStation(),
                        section.getDownStation()
                ))
                .distinct()
                .collect(Collectors.toList());

        validateContains(stations, source, target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph =
                new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        stations.forEach(graph::addVertex);

        for (Section section : sections) {
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance()
            );
        }

        return StationGraphPath.of(new DijkstraShortestPath<>(graph).getPath(source, target));
    }

    private void validateContains(List<Station> stations, Station source, Station target) {
        if (!stations.containsAll(Arrays.asList(source, target))) {
            throw new RuntimeException("구간 내 존재하지 않는 역입니다.");
        }
    }
}
