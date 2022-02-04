package nextstep.subway.path.domain;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;

public class PathFinder {

    public static final String SAME_STATION_MESSAGE = "출발역과 도착역이 같습니다.";
    public static final String NON_EXIST_PATH_MESSAGE = "출발역과 도착역이 연결되어 있지 않습니다.";

    private PathFinderRequest request;

    private PathFinder(PathFinderRequest request) {
        this.request = request;
    }

    public static PathFinder of(PathFinderRequest request) {
        return new PathFinder(request);
    }

    public GraphPath<Station, DefaultWeightedEdge> searchShortestPath() {
        validateFindBefore(request);
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = getGraphByRequest(request);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(request.startStation, request.endStation);
        validateFindAfter(graphPath);

        return graphPath;
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> getGraphByRequest(PathFinderRequest request) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        request.vertexList.forEach(graph::addVertex);
        request.edgeList
                .forEach(line -> setEdgeWeightBySections(graph, line));

        return graph;
    }

    private void validateFindBefore(PathFinderRequest request) {
        if(request.getStartStation().equals(request.getEndStation())) {
            throw new BadRequestException(SAME_STATION_MESSAGE);
        }
    }

    private void validateFindAfter(GraphPath graphPath) {
        if(Objects.isNull(graphPath) || graphPath.getVertexList().isEmpty()) {
            throw new BadRequestException(NON_EXIST_PATH_MESSAGE);
        }
    }

    private void setEdgeWeightBySections(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Line line) {
        Sections sections = line.getSections();
        sections.stream().forEach(section ->
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    @Getter
    public static class PathFinderRequest {
        private Station startStation;
        private Station endStation;
        private List<Station> vertexList;
        private List<Line> edgeList;

        @Builder
        public PathFinderRequest(Station startStation, Station endStation,
                                 List<Station> vertexList, List<Line> edgeList) {
            this.startStation = startStation;
            this.endStation = endStation;
            this.vertexList = vertexList;
            this.edgeList = edgeList;
        }
    }

}
