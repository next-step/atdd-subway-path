package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathResult;
import nextstep.subway.domain.DijkstraStationPathFind;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFind;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathFinderService {

    private final StationService stationService;
    private final LineRepository lineRepository;

    public PathFinderService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }


    public PathResponse getShortestPath(Long source, Long target) {
        PathFind<Line, Station> pathFind = new DijkstraStationPathFind();

        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        List<Line> lines = lineRepository.findAll();
        pathFind.init(lines);

        PathResult<Station> stationPathResult = pathFind.find(sourceStation, targetStation);
        return new PathResponse(stationPathResult.getPathList(), stationPathResult.getDistance());
    }
}
