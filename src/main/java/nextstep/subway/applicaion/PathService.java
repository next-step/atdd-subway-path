package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse getPath(Long sourceId, Long targetId) {
        List<Line> lines = lineRepository.findAll();
        Station start = stationService.findById(sourceId);
        Station end = stationService.findById(targetId);

        DijkstraShortestPath pathGeneratior = new DijkstraShortestPath(createGraph(lines));

        return createPathResponse(start, end, pathGeneratior);
    }

    private PathResponse createPathResponse(Station start, Station end, DijkstraShortestPath pathGenerator) {
        List<Station> shortestPath = pathGenerator.getPath(start, end).getVertexList();
        List<StationResponse> responses = shortestPath.stream().map(stationService::createStationResponse).collect(Collectors.toList());
        return new PathResponse(responses, (int) pathGenerator.getPathWeight(start, end));
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createGraph(List<Line> lines) {
        Set<Station> stations = getAllStations(lines);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);
        lines.forEach(line -> {
            List<Section> sections = line.getSections();
            sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        });
        return graph;
    }

    private Set<Station> getAllStations(List<Line> lines) {
        Set<Station> stations = new HashSet<>();
        lines.forEach(line -> stations.addAll(line.getStations()));
        return stations;
    }
}
