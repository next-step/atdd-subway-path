package nextstep.subway.path;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JGraphPathFinder implements PathFinder {
    @Override
    public Path shortcut(List<Line> lines,
                         Station source,
                         Station target) {
        if(source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역은 같을 수 없습니다.");
        }
        
        DijkstraShortestPath dijkstraShortestPath = createShortestPath(lines);
        GraphPath path = Optional.ofNullable(dijkstraShortestPath.getPath(source, target)).orElseThrow(() -> new IllegalArgumentException("출발역과 도착역은 연결되어 있어야 합니다."));
        List<Station> shortestPath = path.getVertexList();
        Double shorestDistance = path.getWeight();
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
