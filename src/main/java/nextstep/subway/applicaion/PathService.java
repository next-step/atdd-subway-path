package nextstep.subway.applicaion;

import java.util.List;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PathService {
    private final StationService stationService;
    private final PathFinderService pathFinderService;

    public PathService(
            final StationService stationService,
            final PathFinderService pathFinderService
    ) {
        this.stationService = stationService;
        this.pathFinderService = pathFinderService;
    }

    public PathResponse findPathBy(final long source, final long target) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        return createPathResponse(findPath(sourceStation, targetStation));
    }

    private GraphPath findPath(final Station sourceStation, final Station targetStation) {
        pathFinderService.initGraph();
        return pathFinderService.find(sourceStation, targetStation);
    }

    private PathResponse createPathResponse(final GraphPath graphPath) {
        List<StationResponse> stations = stationService.createStationResponsesBy(graphPath.getVertexList());
        double distance = graphPath.getWeight();
        return new PathResponse(stations, distance);
    }
}
