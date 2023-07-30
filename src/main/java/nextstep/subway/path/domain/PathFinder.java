package nextstep.subway.path.domain;

import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {

    private final WeightedMultigraph<Station, SectionWeightedEdge> graph = new WeightedMultigraph<>(SectionWeightedEdge.class);
    private final List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
        addPath(lines);
    }

    public void addPath(List<Line> lines) {
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(this::addVertex);

        lines.stream()
                .flatMap(x -> x.getSections().getSections().stream())
                .map(SectionWeightedEdge::new)
                .forEach(x -> graph.addEdge(x.getSource(), x.getTarget(), x));
    }

    public GraphPath<Station, SectionWeightedEdge> findPath(Station source, Station target) {
        DijkstraShortestPath<Station, SectionWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, SectionWeightedEdge> path = shortestPath.getPath(source, target);

        return path;
    }

    public Sections getSections(GraphPath<Station, SectionWeightedEdge> graphPath) {
        List<Section> sections = graphPath.getEdgeList().stream()
                .map(SectionWeightedEdge::toSection)
                .collect(Collectors.toList());
        return new Sections(sections);
    }

    private void addVertex(Station vertex) {
        graph.addVertex(vertex);
    }

    public WeightedMultigraph<Station, SectionWeightedEdge> getGraph() {
        return graph;
    }
}
