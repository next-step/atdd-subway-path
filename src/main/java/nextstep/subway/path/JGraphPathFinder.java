package nextstep.subway.path;

import nextstep.subway.line.Line;
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

    private final DijkstraShortestPath dijkstraShortestPath;

    public JGraphPathFinder(List<Line> lines) {
        this.dijkstraShortestPath = createShortestPath(lines);
    }

    @Override
    public Path shortcut(Station upStation,
                         Station downStation) {
        List<Station> shortestPath = dijkstraShortestPath.getPath(upStation, downStation).getVertexList();
        Double shorestDistance = dijkstraShortestPath.getPath(upStation, downStation).getWeight();
        return new Path(shortestPath, shorestDistance);
    }

    private static DijkstraShortestPath createShortestPath(List<Line> lines) {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.forEach(line -> createPath(graph, line.getSections(), line.getSections().stations()));
        return new DijkstraShortestPath(graph);
    }

    private static void createPath(WeightedGraph graph,
                                   Sections sections,
                                   List<Station> stations) {
        stations.forEach(graph::addVertex);
        sections.getAll().forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.distance()));
    }
}
