package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {

    private LineRepository lineRepository;
    private StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse showPaths(Long source, Long target) {

        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        List<Line> lines = lineRepository.findAll();
        checkStationStatus(lines, sourceStation, targetStation);

        SubwayGraph graph = new SubwayGraph(lines);
        PathFinder pathFinder = new PathFinder(graph);
        List<Station> stations = pathFinder.getShortestPath(sourceStation, targetStation);
        int totalDistance = pathFinder.getShortestDistance(sourceStation, targetStation);

        return new PathResponse(stations, totalDistance);
    }

    private void checkStationStatus(List<Line> lines, Station source, Station target) {
        if (isSameStation(source, target)) {
            throw new IllegalArgumentException("IS_SAME_STATION");
        }
        if (haveNotStation(lines, source, target)) {
            throw new IllegalArgumentException("HAVE_NOT_STATION");
        }
    }

    private boolean isSameStation(Station source, Station target) {
        return source.equals(target);
    }

    private boolean haveNotStation(List<Line> lines, Station source, Station target) {
        return lines.stream().anyMatch(line -> line.getStations().equals(source) &&
                line.getStations().equals(target));

    }

}
