package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathFinderResult;
import nextstep.subway.path.ui.FindType;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private WeightedMultigraph<Long, DefaultWeightedEdge> subwayGraph;
    private DijkstraShortestPath<Long, DefaultWeightedEdge> paths;

    public PathFinder() {
    }

    @Deprecated
    public PathFinder(List<Line> lines) {
        subwayGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Line line : lines) {
            final List<LineStation> stationsInOrder = line.getStationInOrder();
            addVerticesToGraph(stationsInOrder);
            setEdges(stationsInOrder);
        }
        paths = new DijkstraShortestPath<>(subwayGraph);
    }

    @Deprecated
    public List<Long> getShortestPath(long source, long destination) {
        return paths.getPath(source, destination).getVertexList();
    }

    @Deprecated
    public double getShortestPathWeight(long source, long destination) {
        return paths.getPathWeight(source, destination);
    }

    @Deprecated
    private void setEdges(List<LineStation> stationsInOrder) {
        // set edges and its weight
        stationsInOrder.stream()
                .filter(lineStation -> lineStation.getPreStationId() != null)
                .forEach(it -> {
                    final DefaultWeightedEdge edge = subwayGraph.addEdge(it.getStationId(), it.getPreStationId());
                    subwayGraph.setEdgeWeight(edge, it.getDistance());
                });
    }

    @Deprecated
    private void addVerticesToGraph(List<LineStation> lineStations) {
        // add vertices to graph
        lineStations.stream()
                .map(LineStation::getStationId)
                .forEach(subwayGraph::addVertex);
    }

    public PathFinderResult findPath(List<LineResponse> lineResponses, long srcStationId, long dstStationId, FindType type) {
        return null;
    }
}
