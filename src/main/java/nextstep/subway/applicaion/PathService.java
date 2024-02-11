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

        final WeightedMultigraph<String, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);

        final Set<Section> sections = getAllSectionsInLines();

        final Map<String, Station> stationSet = new HashMap<>();

        sections.forEach(section -> {
            final String upStationId = section.getUpStation().getId().toString();
            final String downStationId = section.getDownStation().getId().toString();

            stationSet.put(upStationId, section.getUpStation());
            stationSet.put(downStationId, section.getDownStation());

            graph.addVertex(upStationId);
            graph.addVertex(downStationId);

            DefaultWeightedEdge edge = graph.addEdge(upStationId, downStationId);
            graph.setEdgeWeight(edge, section.getDistance());
        });

        final DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        final GraphPath path = dijkstraShortestPath.getPath(request.getSource().toString(), request.getTarget().toString());
        int distance = (int) path.getWeight();
        List<String> stationPath = path.getVertexList();

        return new PathResponse(stationPath.stream().map(stationSet::get).collect(Collectors.toList()), distance);
    }

    private Set<Section> getAllSectionsInLines() {
        return lineRepository
            .findAll().stream().flatMap(line -> line.getSections().stream())
            .collect(Collectors.toSet());
    }

}
