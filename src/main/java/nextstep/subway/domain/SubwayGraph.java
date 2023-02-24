package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class SubwayGraph extends WeightedMultigraph<Station, SectionEdge> {
    protected SubwayGraph(Class<? extends SectionEdge> edgeClass) {
        super(edgeClass);
    }

    public SubwayPath findSubwayPath(Station sourceStation, Station targetStation) {
        validateStation(sourceStation, targetStation);
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(this);
        GraphPath path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        return getSubwayPath(path);
    }

    private void validateStation(Station sourceStation, Station targetStation) {
        if (sourceStation == targetStation) {
            throw new IllegalArgumentException("출발역과 도착역이 같은 경우 경로 조회를 할 수 없습니다.");
        }
    }

    private SubwayPath getSubwayPath(GraphPath path) {
        List<Station> stations = path.getVertexList();
        List<SectionEdge> sectionEdges = path.getEdgeList();
        return new SubwayPath(stations, sectionEdges);
    }

    private void addVertex(List<Line> lines) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .collect(Collectors.toList())
                .forEach(this::addVertex);
    }

    private void addEdge(List<Line> lines) {
        Assert.notNull(lines, "lines must not be null");
        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toList())
                .forEach(this::addEdge);
    }

    private void addEdge(Section section) {
        SectionEdge sectionEdge = new SectionEdge(section);
        addEdge(section.getUpStation(), section.getDownStation(), sectionEdge);
        setEdgeWeight(sectionEdge, section.getDistance());
    }

    public static SubwayGraph of(List<Line> lines) {
        SubwayGraph subwayGraph = new SubwayGraph(SectionEdge.class);
        subwayGraph.addVertex(lines);
        subwayGraph.addEdge(lines);
        return subwayGraph;
    }
}
