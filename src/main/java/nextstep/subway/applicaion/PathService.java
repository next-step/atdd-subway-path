package nextstep.subway.applicaion;

import java.util.List;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.dto.PathDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PathService {
    private final StationService stationService;
    private final PathFinderService pathFinderService;
    private final StationMapper stationMapper;

    public PathService(
            final StationService stationService,
            final PathFinderService pathFinderService,
            final StationMapper stationMapper
    ) {
        this.stationService = stationService;
        this.pathFinderService = pathFinderService;
        this.stationMapper = stationMapper;
    }

    public PathResponse findPathBy(final long source, final long target) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        return createPathResponse(findPath(sourceStation, targetStation));
    }

    private PathDto findPath(final Station sourceStation, final Station targetStation) {
        pathFinderService.initGraph();
        return pathFinderService.find(sourceStation, targetStation);
    }

    private PathResponse createPathResponse(final PathDto pathDto) {
        List<StationResponse> stations = stationMapper.toResponseFrom(pathDto.getNodes());
        double distance = pathDto.getWeight();
        return new PathResponse(stations, distance);
    }
}
