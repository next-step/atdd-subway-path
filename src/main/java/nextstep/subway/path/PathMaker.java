package nextstep.subway.path;

import nextstep.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathMaker {
    private List<Line> lines;

    public PathMaker(List<Line> lines) {
        this.lines = lines;
    }

    public GraphPath findShortestPath(Station sourceStation, Station targetStation) {
        validIsSameSourceAndTarget(sourceStation, targetStation);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = makeGraph(lines);

        validSourceAndTargetConnected(graph, sourceStation, targetStation);

        return new DijkstraShortestPath(graph).getPath(sourceStation, targetStation);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> makeGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for(Line line : lines) {
            List<Station> stations = line.getStations();
            addVertex(graph, stations);

            List<Section> sections = line.getSectionList();
            addEdge(graph, sections);
        }

        return graph;
    }

    private void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Station> stations) {
        stations.stream()
                .filter(station -> !graph.containsVertex(station))
                .forEach(graph::addVertex);
    }

    private void addEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        sections.stream()
                .filter(section -> !graph.containsEdge(section.getUpStation(), section.getDownStation()))
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private void validIsSameSourceAndTarget(Station sourceStation, Station targetStation) {
        if(sourceStation.equals(targetStation)) {
            throw new BadRequestException("출발역과 도착역이 같은 경로는 조회할 수 없습니다.");
        }
    }

    private void validSourceAndTargetConnected(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station sourceStation, Station targetStation) {
        GraphPath<Station, DefaultWeightedEdge> edges = DijkstraShortestPath.findPathBetween(graph, sourceStation, targetStation);
        if(edges == null) {
            throw new BadRequestException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
    }
}
