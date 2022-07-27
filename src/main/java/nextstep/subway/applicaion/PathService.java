package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BadRequestException;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        List<Line> lines =lineRepository.findAll();
        Station sourceStation = stationService.findById(sourceStationId);
        Station targetStation = stationService.findById(targetStationId);

        isValid(sourceStation, targetStation);
        PathFinder pathFinder = new PathFinder(lines);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = pathFinder.getShortestPath(sourceStation, targetStation);
        return new PathResponse(createStationResponses(shortestPath.getVertexList()), shortestPath.getWeight());
    }

    private void isValid(Station sourceStation, Station targetStation){
        if(Objects.equals(null, sourceStation)){
            throw new BadRequestException("존재하지 않는 출발역 입니다.");
        }
        if(Objects.equals(null, targetStation)){
            throw new BadRequestException("존재하지 않는 도착역 입니다.");
        }
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream()
                .map(stationService::createStationResponse)
                .collect(Collectors.toList());
    }
}
