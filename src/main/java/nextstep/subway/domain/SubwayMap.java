package nextstep.subway.domain;

import nextstep.subway.domain.vo.Path;
import nextstep.subway.exception.NotRegisteredInAllSectionsException;
import nextstep.subway.exception.SourceAndTargetNotLinkedException;
import nextstep.subway.exception.SourceAndTargetSameException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SubwayMap {

    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public SubwayMap(List<Line> lines) {
        dijkstraShortestPath = dijkstraShortestPath(lines);
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath(List<Line> lines) {
        List<Section> sections = allSectionsFrom(lines);
        WeightedMultigraph<Station, DefaultWeightedEdge> weightedMultiGraph = toWeightedMultiGraph(sections);
        return new DijkstraShortestPath<>(weightedMultiGraph);
    }

    private List<Section> allSectionsFrom(List<Line> lines) {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    private WeightedMultigraph<Station, DefaultWeightedEdge> toWeightedMultiGraph(List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sections.forEach(section -> setupVertexAndEdge(graph, section));
        return graph;
    }


    private void setupVertexAndEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
    }


    public Path shortestPath(Station source, Station target) {
        checkSourceAndTargetDifferent(source, target);
        GraphPath<Station, DefaultWeightedEdge> path;
        try {
            path = dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException e) {
            throw new NotRegisteredInAllSectionsException("출발역 또는 도착역이 존재하지 않습니다.");
        }
        checkPathExist(path);
        List<Station> stations = path.getVertexList();
        int distance = (int) path.getWeight();
        return new Path(stations, distance);
    }


    private void checkSourceAndTargetDifferent(Station source, Station target) {
        if (Objects.equals(source, target)) {
            throw new SourceAndTargetSameException("구간 조회 시 출발역과 도착역이 같을 수 없습니다.");
        }
    }

    private void checkPathExist(GraphPath<Station, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new SourceAndTargetNotLinkedException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
    }

}
