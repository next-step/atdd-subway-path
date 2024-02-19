package nextstep.subway.domain.path;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.path.PathNotFoundException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PathFinderDijkstraStrategy implements PathFinderStrategy {
    @Override
    public Path findShortestPathAndItsDistance(List<Line> lines, Station sourceStation, Station targetStation) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = buildGraph(lines);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);

        if (path == null) {
            throw new PathNotFoundException("주어진 출발역과 도착역은 연결된 구간이 없습니다.");
        }

        List<Station> stations = path.getVertexList();
        double distance = path.getWeight();

        return new Path(stations, (int) distance);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> buildGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        List<Station> allExistingStations = lines.stream()
                .flatMap(it -> it.getAllStations().stream())
                .distinct()
                .collect(Collectors.toList());

        // XXX: allSections와 allStations를 받아 처리하는 것이 좋겠다. 다익스트라 모듈은 그래프 관리만 집중하도록.
        allExistingStations.forEach(graph::addVertex);
        return setWeight(graph, lines);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> setWeight(
            WeightedMultigraph<Station, DefaultWeightedEdge> graph,
            List<Line> lines
    ) {
        List<Section> sections = lines
                .stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toList());

        sections.forEach(section -> {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        });

        return graph;
    }
}
