package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.ShortestPathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    StationService stationService;

    LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public ShortestPathResponse getShortestPath(Long source, Long target) {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = createGraph();

        List<String> shortestPath = getShortestPathList(graph, source, target);
        int shortestLength = getShortestPathLength(graph, source, target);
        List<Station> stations = shortestPath.stream().map(this::convert).collect(Collectors.toList());
        return new ShortestPathResponse(StationResponse.ofList(stations), shortestLength);
    }

    private List<String> getShortestPathList(WeightedMultigraph<String, DefaultWeightedEdge> graph, Long source, Long target){
        return new DijkstraShortestPath(graph).getPath(source.toString(), target.toString()).getVertexList();
    }

    private int getShortestPathLength(WeightedMultigraph<String, DefaultWeightedEdge> graph, Long source, Long target){
        return (int) new DijkstraShortestPath(graph)
                .getPath(source.toString(), target.toString()).getWeight();
    }

    private Station convert(String idAsStr){
        return stationService.findById(Long.parseLong(idAsStr));
    }


    private WeightedMultigraph<String, DefaultWeightedEdge> createGraph() {
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addVertex(graph);
        setEdgeWeight(graph);
        return graph;
    }

    private void addVertex(WeightedMultigraph<String, DefaultWeightedEdge> graph) {
        List<StationResponse> stationResponses = stationService.findAllStations();

        for (StationResponse stationResponse : stationResponses) {
            graph.addVertex(stationResponse.getId().toString());
        }
    }

    private void setEdgeWeight(WeightedMultigraph<String, DefaultWeightedEdge> graph) {
        List<Line> lines = lineService.findLines();

        for (Line line : lines) {
            setEdgeWeight(line, graph);
        }
    }

    private void setEdgeWeight(Line line, WeightedMultigraph<String, DefaultWeightedEdge> graph) {

        for (Section section : line.getSections().getSectionList()) {
            graph.setEdgeWeight(
                    graph.addEdge(
                            section.getUpStation().getId().toString(),
                            section.getDownStation().getId().toString()
                    )
                    , section.getDistance()
            );
        }
    }
}
