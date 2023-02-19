package nextstep.subway.infra;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.dto.PathResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nextstep.subway.common.constants.ErrorConstant.*;

@Component
public class DijkstraShortestPathFinder implements PathFinder {

    private DijkstraShortestPath<Station, DefaultWeightedEdge> path;
    private List<Line> lines = new ArrayList<>();

    @Override
    public void init(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        lines.stream()
                .flatMap(line -> {
                    line.getStations().forEach(graph::addVertex);
                    return line.getSections().stream();
                }).forEach(section -> {
                    graph.addVertex(section.getUpStation());
                    graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
                });

        path = new DijkstraShortestPath<>(graph);
        this.lines.addAll(lines);
    }

    @Override
    public PathResponse find(Station source, Station target) {
        validationFindStation(source, target);

        Optional.ofNullable(path.getPath(source, target))
                .orElseThrow(() -> {
                    throw new IllegalArgumentException(NOT_LINKED_STATION);
                });

        return null;
    }

    private void validationFindStation(Station source, Station target) {
        checkSameStation(source, target);
        checkExistStationsInLines(source, target);
    }

    private void checkSameStation(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(SAME_STATION);
        }
    }

    private void checkExistStationsInLines(Station source, Station target) {
        boolean isContainSource = false;
        boolean isContainTarget = false;

        for (Line line : lines) {
            isContainSource |= line.isContainStation(source);
            isContainTarget |= line.isContainStation(target);

            if (isContainSource && isContainTarget) {
                return;
            }
        }

        throw new IllegalArgumentException(NOT_FOUND_STATION);
    }
}
