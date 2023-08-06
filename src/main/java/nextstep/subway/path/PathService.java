package nextstep.subway.path;

import nextstep.subway.section.SectionRepository;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PathService {
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public PathService(final SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(final Long sourceStationId, final Long targetStationId) {
        GraphPath<String, DefaultWeightedEdge> path = new DijkstraShortestPath<>(createGraph())
                .getPath(sourceStationId.toString(), targetStationId.toString());
        List<StationResponse> pathStations = createStationResponseByGraph(path);

        return new PathResponse(pathStations, (long) path.getWeight());
    }

    private WeightedMultigraph<String, DefaultWeightedEdge> createGraph() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sectionRepository.findAll().forEach(section -> {
            graph.addVertex(section.getUpStation().getId().toString());
            graph.addVertex(section.getDownStation().getId().toString());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation().getId().toString(), section.getDownStation().getId().toString()), section.getDistance());
        });
        return graph;
    }

    private List<StationResponse> createStationResponseByGraph(GraphPath<String, DefaultWeightedEdge> path) {
        List<String> vertexList = path.getVertexList();
        return vertexList.stream()
                .map(Long::parseLong)
                .map(stationRepository::findById)
                .map(Optional::orElseThrow)
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }
}
