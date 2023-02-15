package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.NotFoundException;
import nextstep.subway.domain.exception.SubwayException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

    public PathFinder(List<Line> lines) {
        List<Station> allStations = allStations(lines);
        List<Section> sections = getSections(lines);

        allStations.forEach(graph::addVertex);
        for (Section section : sections) {
            var edge = graph.addEdge(section.getUpStation(), section.getDownStation());
            graph.setEdgeWeight(edge, section.getDistance());
        }
    }

    private List<Station> allStations(List<Line> lines) {
        return lines.stream().map(Line::getStations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Section> getSections(List<Line> lines) {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Path shortestPath(Long source, Long target) {
        Station sourceStation = findStation(source);
        Station targetStation = findStation(target);

        var path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        if (path == null) {
            throw new SubwayException(String.format("%s과 %s이 연결되어있지 않습니다.", sourceStation.getName(), targetStation.getName()));
        }

        return new Path(path.getVertexList(), path.getWeight());
    }

    private Station findStation(Long stationId) {
        return graph.vertexSet()
                .stream()
                .filter(station -> station.getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(stationId + "번 역을 찾을 수 없습니다."));
    }
}
