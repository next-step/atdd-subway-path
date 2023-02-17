package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.dto.PathDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PathService {
    private final StationService stationService;
    private final PathFinderService pathFinderService;
    private final PathMapper pathMapper;

    public PathService(
            final StationService stationService,
            final PathFinderService pathFinderService,
            final PathMapper pathMapper
    ) {
        this.stationService = stationService;
        this.pathFinderService = pathFinderService;
        this.pathMapper = pathMapper;
    }

    public PathResponse findPathBy(final long source, final long target) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        return pathMapper.toResponseFrom(findPath(sourceStation, targetStation));
    }

    private PathDto findPath(final Station sourceStation, final Station targetStation) {
        pathFinderService.initGraph();
        return pathFinderService.find(sourceStation, targetStation);
    }
}
