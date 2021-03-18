package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.exception.NotFoundException;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.FirstStationEqualsFinalStationException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PathService {

    private LineService lineService;
    private StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(long sourceId, long targetId) {
        Station source = stationService.findStationById(sourceId);
        Station target = stationService.findStationById(targetId);

        if (Objects.isNull(source) || Objects.isNull(target)) {
            throw new NotFoundException("존재하지 않는 역 입니다.");
        }

        if (source.equals(target)) {
            throw new FirstStationEqualsFinalStationException();
        }

        return findPath(source, target);
    }

    private PathResponse findPath(Station source, Station target) {
        List<Line> lines = lineService.findAll();

        PathFinder pathFinder = new PathFinder(lines);
        pathFinder.initialize();

        GraphPath path = pathFinder.getShortestPath(source, target);
        List<Station> findShortestPath = path.getVertexList();

        return PathResponse.of(findShortestPath.stream()
                .map(station -> StationResponse.of(station))
                .collect(toList()), (int) path.getWeight());
    }
}
