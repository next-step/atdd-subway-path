package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class SubwayGraph {
    private WeightedMultigraph<Station, SubwayEdge> graph;

    public SubwayGraph(List<Line> lines, PathType type) {
        graph = new WeightedMultigraph(SubwayEdge.class);

        // 지하철 역(정점)을 등록
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .distinct()
                .collect(Collectors.toList())
                .forEach(it -> graph.addVertex(it));

        // 지하철 역의 연결 정보(간선)을 등록
        lines.stream()
                .flatMap(it -> it.getSections().getSections().stream())
                .forEach(it -> graph.setEdgeWeight(createEdge(it), type.findWeightOf(it)));
    }

    private SubwayEdge createEdge(Section section) {
        SubwayEdge subwayEdge = graph.addEdge(section.getUpStation(), section.getDownStation());
        subwayEdge.addSection(section);
        return subwayEdge;
    }

    public PathResult findPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new InvalidPathException("출발역과 도착역이 같습니다.");
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath<Station, SubwayEdge> result = dijkstraShortestPath.getPath(source, target);
        List<Section> sections = result.getEdgeList().stream()
                .map(it -> it.getSection())
                .collect(Collectors.toList());

        return new PathResult(new Sections(sections));
    }
}
