package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(Long source, Long target) {
        Station startStation = stationRepository.findById(source).orElseThrow(IllegalArgumentException::new);
        Station endStation = stationRepository.findById(target).orElseThrow(IllegalArgumentException::new);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath(makeGraph());

        return makeResponse(dijkstraShortestPath.getPath(startStation, endStation));
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> makeGraph() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        List<Line> allLines = lineRepository.findAll();

        for (Line line : allLines) {
            List<Section> sections = line.getSections();

            for (Section section : sections) {
                graph.addVertex(section.getUpStation());
                graph.addVertex(section.getDownStation());
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            }
        }

        return graph;
    }

    private PathResponse makeResponse(GraphPath<Station, DefaultWeightedEdge> path) {
        List<StationResponse> stationResponses = makeStationResponses(path.getVertexList());
        return new PathResponse(stationResponses, (long) path.getWeight());
    }

    private List<StationResponse> makeStationResponses(List<Station> stations) {
        return stations.stream()
                .map(s -> new StationResponse(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }
}
