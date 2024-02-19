package nextstep.subway.path.service;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.SameFindPathStationsException;
import nextstep.subway.exception.UnconnectedFindPathStationsException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PathFinder {

    public FindPathResponse findShortestPath(List<Line> lines, Station startStation, Station endStation) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        addVertex(lines, graph);
        setEdgeWeight(lines, graph);

        validate(lines, startStation, endStation);

        GraphPath shortestPath = new DijkstraShortestPath(graph).getPath(startStation, endStation);

        if (Objects.isNull(shortestPath)) {
            throw new UnconnectedFindPathStationsException();
        }

        List<Station> shortestPathStations = shortestPath.getVertexList();
        double shortestPathWeight = shortestPath.getWeight();

        return FindPathResponse.of(shortestPathStations, (int) shortestPathWeight);
    }

    private void validate(List<Line> lines, Station startStation, Station endStation) {
        if (hasMissStation(lines, startStation) || hasMissStation(lines, endStation)) {
            throw new NotFoundStationException();
        }
        if (startStation.equals(endStation)) {
            throw new SameFindPathStationsException();
        }
    }

    private List<Station> getStations(List<Line> lines) {
        return lines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean hasMissStation(List<Line> lines, Station station) {
        return getStations(lines).stream()
                .noneMatch(s -> s.equals(station));
    }

    private void addVertex(List<Line> lines, WeightedMultigraph graph) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .forEach(graph::addVertex);
    }

    private void setEdgeWeight(List<Line> lines, WeightedMultigraph graph) {
        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

}
