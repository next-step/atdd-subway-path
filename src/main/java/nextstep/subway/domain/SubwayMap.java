package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class SubwayMap {
    private final List<Line> lines;

    public SubwayMap(List<Line> lines) {
        this.lines = lines;
    }

    public Path findPath(Station source, Station target) {
        WeightedMultigraph<Station, SectionEdge> graph = createGraph();

        DijkstraShortestPath<Station, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        List<Section> sections = dijkstraShortestPath.getPath(source, target).getEdgeList().stream()
                .map(SectionEdge::getSection)
                .collect(Collectors.toList());

        return new Path(new Sections(sections));
    }

    private WeightedMultigraph<Station, SectionEdge> createGraph() {
        WeightedMultigraph graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        addVertexes(graph);
        setEdgeWeight(graph);

        return graph;
    }

    private void addVertexes(WeightedMultigraph<Station, SectionEdge> graph) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .forEach(graph::addVertex);
    }

    private void setEdgeWeight(WeightedMultigraph<Station, SectionEdge> graph) {
        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .forEach(section -> {
                    SectionEdge sectionEdge = new SectionEdge(section);
                    graph.addEdge(section.getUpStation(), section.getDownStation(), sectionEdge);
                    graph.setEdgeWeight(sectionEdge, section.getDistance());
                });
    }

    private static class SectionEdge extends DefaultWeightedEdge {
        private final Section section;

        private SectionEdge(Section section) {
            this.section = section;
        }

        private Section getSection() {
            return section;
        }
    }
}
