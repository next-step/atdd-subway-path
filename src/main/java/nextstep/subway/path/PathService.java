package nextstep.subway.path;

import nextstep.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PathService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(Long sourceId, Long targetId) {
        Station startStation = stationRepository.findById(sourceId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 출발역입니다.")
        );
        Station endStation = stationRepository.findById(targetId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 도착역입니다.")
        );

        validStartAndEndStation(startStation, endStation);

        List<Line> lines = lineRepository.findAll();

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = PathMaker.makeGraph(lines);

        validConnectedStation(graph, startStation, endStation);

        GraphPath shortestPath = new DijkstraShortestPath(graph).getPath(startStation, endStation);

        return PathMaker.createPathResponse(shortestPath);
    }

    public void validStartAndEndStation(Station startStation, Station endStation) {
        if(startStation.equals(endStation)) {
            throw new BadRequestException("출발역과 도착역이 같은 경로는 조회할 수 없습니다.");
        }
    }

    public void validConnectedStation(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station startStation, Station endStation) {
        GraphPath<Station, DefaultWeightedEdge> edges = DijkstraShortestPath.findPathBetween(graph, startStation, endStation);
        if(edges == null) {
            throw new BadRequestException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
    }
}
