package nextstep.subway.path;

import nextstep.subway.section.SectionRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
        assertParameterValid(sourceStationId, targetStationId);

        GraphPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(createGraph())
                .getPath(stationRepository.getReferenceById(sourceStationId), stationRepository.getReferenceById(targetStationId));

        List<StationResponse> pathStations = createStationResponseByGraph(path);

        return new PathResponse(pathStations, (long) path.getWeight());
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createGraph() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sectionRepository.findAll().forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        });
        return graph;
    }

    private List<StationResponse> createStationResponseByGraph(GraphPath<Station, DefaultWeightedEdge> path) {
        List<Station> vertexList = path.getVertexList();
        return vertexList.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }


    private void assertParameterValid(Long sourceStationId, Long targetStationId) {
        if (Objects.equals(sourceStationId, targetStationId)) {
            throw new IllegalArgumentException("출발역과 도착역이 동일합니다.");
        }

        if (stationRepository.findById(sourceStationId).isEmpty() || stationRepository.findById(targetStationId).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
    }
}
