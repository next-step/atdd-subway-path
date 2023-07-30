package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PathService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse getPath(Long sourceId, Long targetId) {
        List<Line> lines = lineRepository.findAll();
        List<Long> shortestPath = getShortestPath(lines, sourceId, targetId);

        List<StationResponse> stationResponses = shortestPath.stream()
                .map(stationId -> {
                    Station station = stationRepository.findById(stationId).orElseThrow(NoSuchElementException::new);

                    return new StationResponse(station.getId(), station.getName());
                }).collect(Collectors.toList());

        return new PathResponse(stationResponses);
    }

    List<Long> getShortestPath(List<Line> lines, Long source, Long target) {
        List<Section> allSections = new ArrayList<>();

        lines.forEach(
                line -> {
                    allSections.addAll(line.getSections());
                }
        );

        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        for (Section s : allSections) {
            graph.addVertex(s.getUpStationId());
            graph.addVertex(s.getDownStationId());
            graph.setEdgeWeight(graph.addEdge(s.getUpStationId(), s.getDownStationId()), s.getDistance());
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Long> vertexList = dijkstraShortestPath.getPath(source, target).getVertexList();

        return vertexList;
    }
}
