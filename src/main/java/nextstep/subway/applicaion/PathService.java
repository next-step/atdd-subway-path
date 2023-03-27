package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final StationService stationService;
    private final LineRepository lineRepository;

    public PathService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    public PathResponse searchPath(Long source, Long target) {
        if(source.equals(target)){
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }

        List<Line> lines = lineRepository.findAll();
        SubwayMap subwayMap = new SubwayMap(lines);

        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        int distance = subwayMap.calculateDistance(sourceStation, targetStation);
        List<Long> path = subwayMap.findPath(sourceStation, targetStation);

        List<StationResponse> pathResult = path.stream()
            .map(it -> StationResponse.of(stationService.findById(it)))
            .collect(Collectors.toList());

        return PathResponse.of(pathResult, distance);
    }

}
