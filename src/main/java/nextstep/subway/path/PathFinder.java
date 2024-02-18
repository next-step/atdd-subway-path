package nextstep.subway.path;

import nextstep.subway.Exception.ErrorCode;
import nextstep.subway.Exception.SubwayException;
import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {
    private final List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
    }

    public PathResponse shortestPath(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new SubwayException(ErrorCode.CANNOT_FIND_SHORTEST_PATH, "출발역과 도착역이 같습니다.");
        }

        List<Section> sections = allSections();
        List<Station> stations = allStations(sections);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = makeWeightedMultigraph(stations, sections);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);

        if (shortestPath == null) {
            throw new SubwayException(ErrorCode.CANNOT_FIND_SHORTEST_PATH, "연결되지 않은 역 정보입니다.");
        }
        return new PathResponse(shortestPath.getVertexList(), (long) dijkstraShortestPath.getPathWeight(sourceStation, targetStation));
    }

    private List<Section> allSections() {
        return lines.stream()
                .flatMap(line -> line.getSections().get().stream())
                .collect(Collectors.toList());
    }

    private static List<Station> allStations(List<Section> sections) {
        return sections.stream()
                .flatMap(section -> section.stations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private static WeightedMultigraph<Station, DefaultWeightedEdge> makeWeightedMultigraph(List<Station> stations, List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        return graph;
    }
}
