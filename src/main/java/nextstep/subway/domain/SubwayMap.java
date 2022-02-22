package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.List;
import java.util.stream.Collectors;

public class SubwayMap {
    private final List<Line> lines;

    public SubwayMap(List<Line> lines) {
        this.lines = lines;
    }

    public Path findPath(Station source, Station target) {
        SimpleDirectedWeightedGraph<Station, SectionEdge> graph = createGraph();

        DijkstraShortestPath<Station, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        List<Section> sections = dijkstraShortestPath.getPath(source, target).getEdgeList().stream()
                .map(SectionEdge::getSection)
                .collect(Collectors.toList());

        return new Path(new Sections(sections));
    }

    private SimpleDirectedWeightedGraph<Station, SectionEdge> createGraph() {
        SimpleDirectedWeightedGraph graph = new SimpleDirectedWeightedGraph(DefaultWeightedEdge.class);

        addVertexes(graph);
        setEdgeWeight(graph);
        setOppositeEdgeWeight(graph);

        return graph;
    }

    private void addVertexes(SimpleDirectedWeightedGraph<Station, SectionEdge> graph) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .forEach(graph::addVertex);
    }

    private void setEdgeWeight(SimpleDirectedWeightedGraph<Station, SectionEdge> graph) {
        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .forEach(section -> {
                    SectionEdge sectionEdge = new SectionEdge(section);
                    graph.addEdge(section.getUpStation(), section.getDownStation(), sectionEdge);
                    graph.setEdgeWeight(sectionEdge, section.getDistance());
                });
    }

    private void setOppositeEdgeWeight(SimpleDirectedWeightedGraph<Station, SectionEdge> graph) {
        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .map(section -> new Section(
                        section.getLine(),
                        section.getDownStation(),
                        section.getUpStation(),
                        section.getDistance()))
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
