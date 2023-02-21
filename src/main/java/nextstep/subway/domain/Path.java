package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.exception.CantNotFindPathSameSourceTargetStationException;
import nextstep.subway.domain.exception.NotFoundSourceAndTargetStationException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class Path {

    private final List<StationResponse> stationResponses;
    private final List<SectionResponse> sectionResponses;
    private final DijkstraShortestPath<StationResponse, DefaultWeightedEdge> stationPath;

    public Path(List<StationResponse> stationResponses, List<SectionResponse> sectionResponses) {
        this.stationResponses = stationResponses;
        this.sectionResponses = sectionResponses;
        this.stationPath = process();
    }

    public PathResponse findPath(StationResponse sourceStation, StationResponse targetStation) {
        validatePath(sourceStation, targetStation);

        try {
            List<StationResponse> vertexList = stationPath.getPath(sourceStation, targetStation).getVertexList();
            int pathWeight = (int) stationPath.getPathWeight(sourceStation, targetStation);
            return new PathResponse(vertexList, pathWeight);
        } catch (IllegalArgumentException e) {
            throw new NotFoundSourceAndTargetStationException();
        }
    }

    private void validatePath(StationResponse sourceStation, StationResponse targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new CantNotFindPathSameSourceTargetStationException();
        }
    }

    private DijkstraShortestPath<StationResponse, DefaultWeightedEdge> process() {
        WeightedMultigraph<StationResponse, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        stationResponses.forEach(graph::addVertex);
        sectionResponses.forEach(sectionResponse ->
            setEdgeWeight(graph, sectionResponse)
        );

        return new DijkstraShortestPath<>(graph);
    }

    private void setEdgeWeight(WeightedMultigraph<StationResponse, DefaultWeightedEdge> graph, SectionResponse sectionResponse) {
        graph.setEdgeWeight(graph.addEdge(sectionResponse.getUpStation(), sectionResponse.getDownStation()), sectionResponse.getDistance());
    }

}
