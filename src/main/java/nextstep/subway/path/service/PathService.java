package nextstep.subway.path.service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.service.LineProvider;
import nextstep.subway.path.exception.PathNotFoundException;
import nextstep.subway.path.service.dto.PathResponse;
import nextstep.subway.path.service.dto.PathSearchRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotExistException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineProvider lineProvider;

    public PathService(final LineProvider lineProvider) {
        this.lineProvider = lineProvider;
    }

    public PathResponse findShortestPath(final PathSearchRequest searchRequest) {
        searchRequest.validate();

        final List<Line> allLines = lineProvider.getAllLines();
        final Map<Long, Station> stationMap = createStationMapFrom(allLines);
        final Station sourceStation = stationMap.computeIfAbsent(searchRequest.getSource(), throwStationNowFoundException());
        final Station targetStation = stationMap.computeIfAbsent(searchRequest.getTarget(), throwStationNowFoundException());

        final DijkstraShortestPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(buildGraph(allLines));
        final GraphPath<Station, DefaultWeightedEdge> shortestPath =
                Optional.ofNullable(path.getPath(sourceStation, targetStation))
                        .orElseThrow(PathNotFoundException::new);

        return PathResponse.of(shortestPath.getVertexList(), (int) shortestPath.getWeight());
    }

    private Map<Long, Station> createStationMapFrom(final List<Line> allLines) {
        return allLines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .collect(Collectors.toMap(Station::getId, Function.identity()));
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> buildGraph(final List<Line> allLines) {
        final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        allLines.stream()
                .flatMap(line -> line.getSections().stream())
                .forEach(section -> initGraph(section, graph));
        return graph;
    }

    private void initGraph(final Section section, final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
    }

    private static Function<Long, Station> throwStationNowFoundException() {
        return id -> {
            throw new StationNotExistException(id);
        };
    }
}
