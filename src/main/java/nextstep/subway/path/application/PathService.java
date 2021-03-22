package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.dto.ShortestPathResponse;
import nextstep.subway.path.exception.SameSourceTargetException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final StationService stationService;

    private final LineService lineService;

    private Path path;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
        path = new Path();
    }

    public ShortestPathResponse getShortestPath(long source, long target) {

        if (!validateStationExist(source) && !validateStationExist(target)) {
            throw new NoSuchElementException("출발역 또는 도착역이 없습니다");
        }

        WeightedMultigraph<String, DefaultWeightedEdge> graph = createGraph();

        int shortestLength = path.getShortestPathLength(graph, source, target);
        List<String> shortestPath = path.getShortestPathList(graph, source, target);
        List<Station> stations = shortestPath
                .stream()
                .map(this::stringIdToStation)
                .collect(Collectors.toList());

        return new ShortestPathResponse(StationResponse.ofList(stations), shortestLength);
    }

    private WeightedMultigraph<String, DefaultWeightedEdge> createGraph() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addVertex(graph);
        setEdgeWeight(graph);
        return graph;
    }

    private void addVertex(WeightedMultigraph<String, DefaultWeightedEdge> graph) {
        List<StationResponse> stationResponses = stationService.findAllStations();
        path.addVertex(graph, stationResponses);
    }

    private void setEdgeWeight(WeightedMultigraph<String, DefaultWeightedEdge> graph) {
        List<Line> lines = lineService.findLines();
        path.setEdgeWeight(graph, lines);
    }

    private boolean validateStationExist(Long id) {
        return stationService.isExist(id);
    }

    private Station stringIdToStation(String idAsStr) {
        return stationService.findStationById(Long.parseLong(idAsStr));
    }
}
