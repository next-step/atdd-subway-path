package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.global.error.exception.ErrorCode;
import nextstep.subway.global.error.exception.InvalidValueException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class JGraphPathFinder {

    public PathResponse find(List<Line> lines, Station source, Station target) {
        checkSameStations(source, target);
        checkExistStationsInLines(lines, source, target);
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = makeSubwayGraph(lines);
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        int distance = (int) dijkstraShortestPath.getPathWeight(source, target);
        List<Station> paths = checkConnectedStationsAndGetPaths(source, target, dijkstraShortestPath);
        return getPathResponse(distance, paths);
    }

    private PathResponse getPathResponse(int distance, List<Station> paths) {
        return PathResponse.builder()
                .stations(paths)
                .distance(distance)
                .build();
    }

    private List checkConnectedStationsAndGetPaths(Station source, Station target, DijkstraShortestPath dijkstraShortestPath) {
        return Optional.ofNullable(dijkstraShortestPath.getPath(source, target))
                .map(graphPath -> graphPath.getVertexList())
                .orElseThrow(() -> {
                    throw new InvalidValueException(ErrorCode.STATIONS_NOT_CONNECTED);
                });
    }

    private void checkSameStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new InvalidValueException(ErrorCode.START_STATION_MUST_NOT_SAME_END_STATION);
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> makeSubwayGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.stream()
                .map(line -> line.getSections().getSections())
                .flatMap(Collection::stream)
                .forEach(section -> create(graph, section));
        return graph;
    }

    private void create(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();
        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), distance);
    }

    private void checkExistStationsInLines(List<Line> lines, Station source, Station target) {
        boolean isContainSource = false;
        boolean isContainTarget = false;
        for (Line line : lines) {
            isContainSource |= line.isContainStation(source);
            isContainTarget |= line.isContainStation(target);

            if (isContainSource && isContainTarget) {
                return;
            }
        }
        throw new InvalidValueException(ErrorCode.NOT_EXISTS_STATIONS);
    }
}
