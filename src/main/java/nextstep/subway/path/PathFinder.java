package nextstep.subway.path;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;

public class PathFinder {
    private final DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(WeightedGraph graph,
                      List<Line> lines) {
        lines.forEach(line -> createPath(graph, line.getSections(), line.getSections().stations()));
        this.dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    private void createPath(WeightedGraph graph,
                                            Sections sections,
                                            List<Station> stations) {
        stations.forEach(graph::addVertex);
        sections.getAll().forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.distance()));
    }

    public Path shortcut(Station upStation,
                         Station downStation) {
        List<Station> shortestPath = dijkstraShortestPath.getPath(upStation, downStation).getVertexList();
        Double shorestDistance = dijkstraShortestPath.getPath(upStation, downStation).getWeight();
        return new Path(shortestPath, shorestDistance);
    }
}
