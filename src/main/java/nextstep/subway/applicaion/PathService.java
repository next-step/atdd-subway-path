package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineSectionResponse;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse getPaths(Long source, Long target) {
        List<LineSectionResponse> lineSectionResponses = lineService.getAllLineSections();

        WeightedMultigraph<StationResponse, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        List<StationResponse> stations = stationService.findAllStations();
        stations.forEach(graph::addVertex);

        StationResponse sourceStation = stations.stream().filter(stationResponse -> stationResponse.getId().equals(source)).findFirst().orElseThrow(IllegalAccessError::new);
        StationResponse targetStation = stations.stream().filter(stationResponse -> stationResponse.getId().equals(target)).findFirst().orElseThrow(IllegalAccessError::new);

        HashSet<StationResponse> set = new HashSet<>();

        lineSectionResponses.forEach(lineSectionResponse ->
            lineSectionResponse.getSectionResponses().forEach(sectionResponse -> {
                set.add(sectionResponse.getUpStation());
                set.add(sectionResponse.getDownStation());
            }));

        set.forEach(graph::addVertex);

        lineSectionResponses.forEach(lineSectionResponse -> lineSectionResponse.getSectionResponses()
            .forEach(sectionResponse -> graph.setEdgeWeight(graph.addEdge(sectionResponse.getUpStation(), sectionResponse.getDownStation()), sectionResponse.getDistance())));

        DijkstraShortestPath<StationResponse, DefaultWeightedEdge> stationPath = new DijkstraShortestPath<>(graph);

        List<StationResponse> stationResponses = stationPath.getPath(sourceStation, targetStation).getVertexList();
        int pathWeight = (int) (stationPath.getPathWeight(sourceStation, targetStation));
        return new PathResponse(stationResponses, pathWeight);
    }

}
