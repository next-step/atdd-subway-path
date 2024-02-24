package nextstep.subway.path;

import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class PathFinder {

    public Path findPath(List<Line> lines, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        
        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .distinct()
                .forEach(section -> {
                    Station upStation = section.getUpStation();
                    Station downStation = section.getDownStation();
                    graph.addVertex(downStation);
                    graph.addVertex(upStation);
                    graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
                });

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath path = Optional.ofNullable(dijkstraShortestPath.getPath(source, target)).orElseThrow(() -> new SubwayException("출발역과 도착역은 연결되어 있어야 합니다."));

        return new Path(path.getVertexList(), path.getWeight());
    }
}
