package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
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

    public PathResponse findShortestPath(Long source, Long target) {
        Station startStation = stationService.findById(source);
        Station arrivalStation = stationService.findById(target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        List<Line> lines = lineService.getAllLines();

        for (Line line : lines) {
            line.getStations().forEach(s -> graph.addVertex(s));
        }

        List<Section> sectionList = lines.stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toList());

        sectionList.forEach(section ->
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance())
        );

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> shortestPath = dijkstraShortestPath.getPath(startStation, arrivalStation).getVertexList();
        List<StationResponse> stationResponses = shortestPath.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        int distance = (int) dijkstraShortestPath.getPath(startStation, arrivalStation).getWeight();
        return new PathResponse(stationResponses, distance);
    }
}
