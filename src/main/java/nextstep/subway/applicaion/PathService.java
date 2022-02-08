package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long startStationId, Long arrivalStationId) {
        Station startStation = stationService.findById(startStationId);
        Station arrivalStation = stationService.findById(arrivalStationId);

        List<Line> lines = lineService.findAllLines();


        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.stream()
                .flatMap(line -> line.stations().stream())
                .forEach(graph::addVertex);

        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .forEach(section -> getEdgeWeight(graph, section));

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(startStation, arrivalStation);
        List<Station> shortestPath = graphPath.getVertexList();
        List<StationResponse> stationResponses = shortestPath.stream()
                .map(this::toStationResponse)
                .collect(Collectors.toList());
        int weight = (int) graphPath.getWeight();

        return new PathResponse(stationResponses, weight);
    }

    private void getEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public StationResponse toStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCreatedDate(),
                station.getModifiedDate()
        );
    }
}
