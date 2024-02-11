package nextstep.subway.applicaion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineRepository lineRepository;

    public PathService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public PathResponse getPath(PathRequest request) {
        validateRequest(request);

        final Set<Section> sections = getAllSectionsInLines();

        final WeightedMultigraph<String, DefaultWeightedEdge> sectionGraph = getWightedGraphWithSection(sections);

        final DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(sectionGraph);

        final GraphPath<String, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(request.getSource().toString(), request.getTarget().toString());

        return getPathResponse(sections, graphPath.getVertexList(), (int) graphPath.getWeight());
    }

    private void validateRequest(PathRequest request) {
        if (request.getSource().equals(request.getTarget())) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }
    }

    private PathResponse getPathResponse(Set<Section> sections, List<String> stationPath, int distance) {
        final Map<String, Station> stationMap = getIdToStationMap(sections);

        return new PathResponse(
            stationPath.stream().map(stationMap::get).collect(Collectors.toList())
            , distance
        );
    }

    private Map<String, Station> getIdToStationMap(Set<Section> sections) {
        final Map<String, Station> stationMap = new HashMap<>();

        sections.forEach(section -> {
            stationMap.put(section.getUpStation().getId().toString(), section.getUpStation());
            stationMap.put(section.getDownStation().getId().toString(), section.getDownStation());
        });

        return stationMap;
    }

    private static WeightedMultigraph<String, DefaultWeightedEdge> getWightedGraphWithSection(
        Set<Section> sections) {
        final WeightedMultigraph<String, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);

        sections.forEach(section -> {
            final String upStationId = section.getUpStation().getId().toString();
            final String downStationId = section.getDownStation().getId().toString();

            graph.addVertex(upStationId);
            graph.addVertex(downStationId);

            DefaultWeightedEdge edge = graph.addEdge(upStationId, downStationId);
            graph.setEdgeWeight(edge, section.getDistance());
        });

        return graph;
    }

    private Set<Section> getAllSectionsInLines() {
        return lineRepository
            .findAll().stream().flatMap(line -> line.getSections().stream())
            .collect(Collectors.toSet());
    }

}
