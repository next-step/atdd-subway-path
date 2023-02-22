package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.exception.CantNotFindPathSameSourceTargetStationException;
import nextstep.subway.domain.exception.NotFoundPathException;
import nextstep.subway.domain.exception.NotFoundSourceAndTargetStationException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class Path {

    private final DijkstraShortestPath<Station, DefaultWeightedEdge> stationPath;

    public Path(List<Station> stationResponses, List<Section> sectionResponses) {
        this.stationPath = process(stationResponses, sectionResponses);
    }

    public PathResponse findPath(Station sourceStation, Station targetStation) {
        validatePath(sourceStation, targetStation);

        try {
            List<StationResponse> vertexList = stationPath.getPath(sourceStation, targetStation).getVertexList()
                .stream().map(StationResponse::from)
                .collect(Collectors.toList());
            int pathWeight = (int) stationPath.getPathWeight(sourceStation, targetStation);
            return new PathResponse(vertexList, pathWeight);
        } catch (IllegalArgumentException e) {
            throw new NotFoundSourceAndTargetStationException();
        } catch (NullPointerException npe) {
            throw new NotFoundPathException();
        }
    }

    private void validatePath(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new CantNotFindPathSameSourceTargetStationException();
        }
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> process(List<Station> stations, List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        stations.forEach(graph::addVertex);
        sections.forEach(section ->
            setEdgeWeight(graph, section)
        );

        return new DijkstraShortestPath<>(graph);
    }

    private void setEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

}
