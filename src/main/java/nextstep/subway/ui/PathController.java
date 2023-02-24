package nextstep.subway.ui;

import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PathController {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathController(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository; 
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> path(@RequestParam Long source, @RequestParam Long target) {
        Station sourceStation = stationRepository.findById(source).orElseThrow(IllegalArgumentException::new);
        Station targetStation = stationRepository.findById(target).orElseThrow(IllegalArgumentException::new);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);

        lineRepository.findAll()
                .stream()
                .flatMap(line -> line.getStations().stream())
                .forEach(station -> graph.addVertex(station));

        lineRepository.findAll()
                .stream()
                .flatMap(line -> line.getSections().stream())
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance().getValue()));

        DijkstraShortestPath dijkstraShortestPath
                = new DijkstraShortestPath(graph);

        List<Station> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
        int shortestDistance = (int) dijkstraShortestPath.getPathWeight(sourceStation, targetStation);

        return ResponseEntity.ok(
                new PathResponse(
                        shortestPath.stream().map(s -> new StationResponse(s.getId(), s.getName())).collect(Collectors.toList()),
                        shortestDistance)
        );
    }
}
