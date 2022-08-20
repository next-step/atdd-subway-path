package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathSearch;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.NotFoundLineException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineRepository lineRepository;
    private StationService stationService;
    private PathSearch pathSearch = new PathSearch();

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    private void registerPaths() {
        List<Line> LinesRegistered = lineRepository.findAll();
        if (LinesRegistered.isEmpty()) {
            throw new NotFoundLineException();
        }
        pathSearch.addPaths(LinesRegistered);
    }

    /**
     * 출발역과 도착역까지의 최단경로 정보를 조회한다.
     * @param source 출발역 id
     * @param target 도착역 id
     */
    public PathResponse showPaths(Long source, Long target) {
        registerPaths();

        Station departure = stationService.findById(source);
        Station destination = stationService.findById(target);

        Double distance = pathSearch.getShortestPathDistance(departure, destination);
        List<String> shortestPath = pathSearch.getShortestPath(departure, destination);

        List<Station> stations = new ArrayList<>();
        for (String stationName : shortestPath) {
            stations.add(stationService.findStationByName(stationName));
        }

        return new PathResponse(stations, distance);
    }
}
