package nextstep.subway.applicaion;

import nextstep.subway.domain.LineGraph;
import nextstep.subway.domain.StationPath;
import nextstep.subway.ui.dto.PathResponse;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(final LineService lineService, final StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse showPath(final Long source, final Long target) {
        final LineGraph lineGraph = new LineGraph(lineService.findLines());

        final StationPath path = lineGraph.findShortestPath(
                stationService.findById(source),
                stationService.findById(target));

        return new PathResponse(path.getStationList(), path.getDistance());
    }
}
