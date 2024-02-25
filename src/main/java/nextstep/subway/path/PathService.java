package nextstep.subway.path;

import java.util.HashMap;
import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

@Service
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(Long sourceId, Long targetId) {
        Station startStation = stationRepository.findById(sourceId)
                                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 출발역입니다."));
        Station endStation = stationRepository.findById(targetId)
                                              .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도착역입니다."));

        validStartAndEndStation(startStation, endStation);

        List<Line> lines = lineRepository.findAll();

        HashMap<Line, List<Station>> lineListHashMap = new HashMap<>();

        for (Line line : lines) {
            List<Long> ids = line.getAllStationId();
            List<Station> stations = stationRepository.findAllById(ids);
            lineListHashMap.put(line, stations);
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = PathManager.makeGraph(lineListHashMap);

        validConnectedStation(graph, startStation, endStation);

        GraphPath shortestPath = new DijkstraShortestPath(graph).getPath(startStation, endStation);
        return PathResponse.createResponse(shortestPath.getVertexList(), shortestPath.getWeight());
    }

    public void validStartAndEndStation(Station startStation, Station endStation) {
        if(startStation.getId().equals(endStation.getId())) {
            throw new IllegalArgumentException("출발역과 도착역이 같은 경로는 조회할 수 없습니다.");
        }
    }

    public void validConnectedStation(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station startStation, Station endStation) {
        GraphPath<Station, DefaultWeightedEdge> edges = DijkstraShortestPath.findPathBetween(graph, startStation, endStation);
        if(edges == null) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
    }
}
