package nextstep.subway.map.application;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.PathResult;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Graph {

    public PathResult findPath(List<LineResponse> lineResponses, Long start, Long target) {
        return null;
    }

}
