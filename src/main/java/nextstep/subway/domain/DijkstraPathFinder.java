package nextstep.subway.domain;

import nextstep.subway.common.exception.SubwayException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

import static nextstep.subway.common.exception.ErrorCode.*;

@Component
public class DijkstraPathFinder implements PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public DijkstraPathFinder(List<Line> lines) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        drawGraph(lines);
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public void drawGraph(List<Line> lines){
        for (Line line : lines) {
            line.getStations().forEach(graph::addVertex);
            line.getSections().forEach(section
                    -> graph.setEdgeWeight(
                            graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        }
    }


    @Override
    public Path findShortPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new SubwayException(NOT_SAME_START_END_STATION);
        }

        GraphPath<Station, DefaultWeightedEdge> path;

        try {
             path = dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException e) {
            throw new SubwayException(NOT_CONNECT_START_END_STATION);
        }

        return new Path(path.getVertexList(), (int) path.getWeight());
    }


}
