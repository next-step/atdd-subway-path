package nextstep.subway.service;

import nextstep.subway.dto.path.PathResponse;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PathService {
    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    /** 경로 조회 */
    public PathResponse getPaths(Long source, Long target) {
        if(Objects.equals(source, target)) {
            throw new IllegalArgumentException("출발역과 도착역은 동일할 수 없다.");
        }

        Station sourceStation = stationService.findStation(source);
        Station targetStation = stationService.findStation(target);

        try {
            WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

            List<Station> stations = stationService.findStations();
            for (Station station : stations) {
                graph.addVertex(station);
            }
            for (Section section : lineService.findSections()) {
                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
                graph.setEdgeWeight(edge, section.getDistance());
            }

            DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
            GraphPath path = dijkstraShortestPath.getPath(sourceStation, targetStation);
            List<Station> shortestPath = path.getVertexList();
            Integer distance = (int) path.getWeight();

            return new PathResponse(shortestPath, distance);
        } catch (Exception e) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있어야 한다.");
        }
    }
}
