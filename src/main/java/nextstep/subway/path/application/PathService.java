package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private LineService lineService;
    private StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);

        List<Section> sections = lineService.findLines().stream()
                .map(it -> it.getSections())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        PathFinder pathFinder = PathFinder.create(sections, source, target);
        pathFinder.getShortestPath();

        return createPathResponse(pathFinder.getShortestPath(), pathFinder.getShortestPathDistance());
    }

    private PathResponse createPathResponse(List<Station> stations, int distance) {
        List<StationResponse> stationReponses = stations.stream()
                .map(it -> StationResponse.of(it))
                .collect(Collectors.toList());

        return new PathResponse(stationReponses, distance);
    }
}
