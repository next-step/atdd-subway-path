package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.dto.PathsDto;
import nextstep.subway.station.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);

    public PathFinder(List<Line> lines) {
        for (Line line : lines) {
            line.getStations()
                    .stream()
                    .distinct()
                    .forEach(this::addVertex);

            line.getLineSections()
                    .forEach(this::addEdge);
        }
    }


    public void addVertex(Station station) {
        graph.addVertex(station);
    }

    public void addEdge(Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public PathsDto findPath(Station start, Station end) {
        if (start.equals(end)) {
            throw new IllegalStateException("시작과 끝이 같은 역입니다");
        }
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath
                = new DijkstraShortestPath<>(graph);
        return new PathsDto(dijkstraShortestPath.getPathWeight(start, end), dijkstraShortestPath.getPath(start, end).getVertexList());
    }

}
