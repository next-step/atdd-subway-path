package nextstep.subway.path;

import nextstep.subway.line.Lines;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;
import nextstep.subway.station.Stations;
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
    public Path shortcut(Lines lines,
                         Station source,
                         Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역은 같을 수 없습니다.");
        }

        if (!lines.existStation(source) || !lines.existStation(target)) {
            throw new IllegalArgumentException("입력한 역을 찾을 수 없습니다.");
        }

        DijkstraShortestPath dijkstraShortestPath = createShortestPath(lines);
        GraphPath path = Optional.ofNullable(dijkstraShortestPath.getPath(source, target)).orElseThrow(() -> new IllegalArgumentException("출발역과 도착역은 연결되어 있어야 합니다."));
        List<Station> shortestPath = path.getVertexList();
        Double shorestDistance = path.getWeight();
        return new Path(shortestPath, shorestDistance);
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> createShortestPath(Lines lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.forEach(line -> createPath(graph, line.getSections(), line.getStations()));
        return new DijkstraShortestPath<>(graph);
    }

    private void createPath(WeightedGraph graph,
                            Sections sections,
                            Stations stations) {
        stations.forEach(graph::addVertex);
        sections.getAll().forEach(section -> setEdge(graph, section));
    }

    private static void setEdge(WeightedGraph graph,
                                Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.distance());
    }
}
