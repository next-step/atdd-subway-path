package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.EqualsStationsException;
import nextstep.subway.station.application.StationService;
import org.springframework.stereotype.Service;

@Service
public class PathService {
    private final LineService lineService;
    private final StationService stationService;
    private final PathFinder pathFinder;

    public PathService(
        LineService lineService,
        StationService stationService,
        PathFinder pathFinder
    ) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathFinder = pathFinder;
    }

    public PathResponse getPath(long sourceId, long targetId) {

        validateDoseNotEquals(sourceId, targetId);

        return PathResponse.of(
            pathFinder.getShortedPath(
                lineService.getAllSections(),
                stationService.findById(sourceId),
                stationService.findById(targetId)
            )
        );

    }

    private void validateDoseNotEquals(long sourceId, long targetId) {
        if (sourceId == targetId) {
            throw new EqualsStationsException();
        }
    }

}
