package nextstep.subway.domain.path;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.PathException;
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
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        List<Station> allExistingStations = lines.stream()
                .flatMap(it -> it.getAllStations().stream())
                .collect(Collectors.toList());

        allExistingStations.forEach(station -> System.out.println(station.getName()));

        allExistingStations.forEach(graph::addVertex);

        lines.forEach(line -> line.getSections().forEach(section -> {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        }));

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);

        if (path == null) {
            throw new PathException("주어진 출발역과 도착역은 연결된 구간이 없습니다.");
        }

        List<Station> stations = path.getVertexList();

        double distance = path.getWeight();

        return new Path(stations, (int) distance);
    }
}
