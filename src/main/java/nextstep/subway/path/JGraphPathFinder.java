package nextstep.subway.path;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JGraphPathFinder implements PathFinder {
    @Override
    public Path shortcut(List<Line> lines,
                         Station source,
                         Station target) {
        DijkstraShortestPath dijkstraShortestPath = createShortestPath(lines);
        List<Station> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();
        Double shorestDistance = dijkstraShortestPath.getPath(source, target).getWeight();
        return new Path(shortestPath, shorestDistance);
    }

    private DijkstraShortestPath createShortestPath(List<Line> lines) {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.forEach(line -> createPath(graph, line.getSections(), line.getSections().stations()));
        return new DijkstraShortestPath(graph);
    }

    private void createPath(WeightedGraph graph,
                            Sections sections,
                            List<Station> stations) {
        stations.forEach(graph::addVertex);
        sections.getAll().forEach(section -> setGraph(graph, section));
    }

    private static void setGraph(WeightedGraph graph,
                                 Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.distance());
    }
}
