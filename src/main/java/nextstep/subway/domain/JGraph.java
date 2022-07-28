package nextstep.subway.domain;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.util.ObjectUtils;

public class JGraph implements GraphStrategy {
    private final List<Line> lines;
    private final WeightedMultigraph<Station, SectionEdge> graph;
    private final DijkstraShortestPath<Station, SectionEdge> shortestPath;


    public JGraph(List<Line> lines) {
        this.lines = lines;
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        initGraph();
        shortestPath = new DijkstraShortestPath(graph);
    }

    @Override
    public List<Station> findShortestPath(Station target, Station source) {
        GraphPath<Station, SectionEdge> graphPath = getGraphPath(target, source);

        return graphPath.getVertexList();
    }

    @Override
    public int getShortestDistance(Station target, Station source) {
        GraphPath<Station, SectionEdge> graphPath = getGraphPath(target, source);

        List<Section> sections = graphPath.getEdgeList().stream()
                .map(SectionEdge::getSection)
                .collect(Collectors.toList());

        return new Sections(sections).getTotalDistance();
    }

    private GraphPath<Station, SectionEdge> getGraphPath(Station target, Station source) {
        checkIsSameTargetAndSource(target, source);

        GraphPath<Station, SectionEdge> path = shortestPath.getPath(target, source);
        checkNotFoundPath(path);



        return path;
    }

    private void checkNotFoundPath(GraphPath<Station, SectionEdge> path) {
        if (ObjectUtils.isEmpty(path)) {
            throw new IllegalArgumentException("출발역과 도착역 경로를 찾을 수 없습니다.");
        }
    }

    private void checkIsSameTargetAndSource(Station target, Station source) {
        if (target.equals(source))
            throw new IllegalArgumentException("출발역과 도착역이 같을 수 없습니다.");
    }

    private void initGraph() {
        initVertex();
        initEdgeWeight();
    }

    private void initVertex() {
        findAllDistinctStations()
                .forEach(graph::addVertex);
    }

    private void initEdgeWeight() {
        findAllSections()
                .forEach(this::setEdgeWeight);
    }

    private void setEdgeWeight(Section section) {
        Distance distance = section.getDistance();
        SectionEdge sectionEdge = SectionEdge.of(section);

        graph.addEdge(section.getUpStation(), section.getDownStation(), sectionEdge);
        graph.setEdgeWeight(sectionEdge, distance.getValue());
    }

    private List<Station> findAllDistinctStations() {
        return lines.stream()
                .map(Line::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Section> findAllSections() {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
