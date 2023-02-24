package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayGraph;
import nextstep.subway.domain.SubwayPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineService lineService;
    private StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findPath(Long source, Long target) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        List<Line> lines = lineService.findAllLines();

        SubwayGraph subwayGraph = SubwayGraph.of(lines);

        SubwayPath subwayPath = subwayGraph.findSubwayPath(sourceStation, targetStation);
        return PathResponse.of(subwayPath);
    }
}
