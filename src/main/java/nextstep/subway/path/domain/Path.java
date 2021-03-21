package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.dto.ShortestPathResponse;
import nextstep.subway.path.exception.SameSourceTargetException;
import nextstep.subway.path.exception.SourceTargetNotReachable;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;

public class Path {

    public List<String> getShortestPathList(WeightedMultigraph<String, DefaultWeightedEdge> graph,
                                            long source, long target) throws NullPointerException {
        GraphPath<String, DefaultWeightedEdge> graphPath = getGraphPath(graph, source, target);
        return graphPath.getVertexList();
    }


    public int getShortestPathLength(WeightedMultigraph<String, DefaultWeightedEdge> graph,
                                     long source, long target) throws NullPointerException {
        GraphPath<String, DefaultWeightedEdge> graphPath = getGraphPath(graph, source, target);
        return (int) graphPath.getWeight();
    }

    public GraphPath<String, DefaultWeightedEdge> getGraphPath(WeightedMultigraph<String, DefaultWeightedEdge> graph, long source, long target){
        validateSourceTargetSame(source, target);
        GraphPath<String, DefaultWeightedEdge> graphPath = new DijkstraShortestPath(graph).getPath(String.valueOf(source), String.valueOf(target));
        validateSourceTargetReachable(graphPath);
        return graphPath;
    }

    public void addVertex(WeightedMultigraph<String, DefaultWeightedEdge> graph, List<StationResponse> stationResponses) {
        for (StationResponse stationResponse : stationResponses) {
            graph.addVertex(stationResponse.getId().toString());
        }
    }

    public void setEdgeWeight(WeightedMultigraph<String, DefaultWeightedEdge> graph, List<Line> lines) {
        for (Line line : lines) {
            setEdgeWeight(graph, line);
        }
    }

    private void setEdgeWeight(WeightedMultigraph<String, DefaultWeightedEdge> graph, Line line) {
        for (Section section : line.getSections().getSectionList()) {
            graph.setEdgeWeight(
                    graph.addEdge(
                            section.getUpStation().getId().toString(),
                            section.getDownStation().getId().toString()
                    )
                    , section.getDistance()
            );
        }
    }

    private void validateSourceTargetSame(long source, long target) {
        if (source == target) {
            throw new SameSourceTargetException("출발역과 도착역이 같습니다");
        }
    }

    private void validateSourceTargetReachable(GraphPath<String, DefaultWeightedEdge> graphPath) {
        if (Objects.isNull(graphPath)) {
            throw new SourceTargetNotReachable("출발역과 도착역이 만날 수 없습니다");
        }
    }
}
