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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathFinder {
    private final Station sourceStation;
    private final Station targetStation;
    private final List<Line> lines;

    public PathFinder(Station sourceStation, Station targetStation, List<Line> lines) {
        if (sourceStation.equals(targetStation)) {
            throw new SubwayException(ErrorCode.CANNOT_FIND_SHORTEST_PATH, "출발역과 도착역이 같습니다.");
        }

        this.sourceStation = sourceStation;
        this.targetStation = targetStation;
        this.lines = lines;
    }

    public PathResponse shortestPath() {
        List<Section> sections = allSections();
        Set<Station> stations = allStations(sections);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = makeWeightedMultigraph(stations, sections);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);

        if (shortestPath == null){
            throw new SubwayException(ErrorCode.CANNOT_FIND_SHORTEST_PATH, "연결되지 않은 역 정보입니다.");
        }
        return new PathResponse(shortestPath.getVertexList(), dijkstraShortestPath.getPathWeight(sourceStation, targetStation));
    }

    private List<Section> allSections() {
        List<Section> sections = new ArrayList<>();
        lines.forEach(line -> sections.addAll(line.getSections().get()));
        return sections;
    }

    private static Set<Station> allStations(List<Section> sections) {
        Set<Station> stations = new HashSet<>();
        sections.forEach(s -> stations.addAll(s.stations()));
        return stations;
    }

    private static WeightedMultigraph<Station, DefaultWeightedEdge> makeWeightedMultigraph(Set<Station> stations, List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        return graph;
    }
}
