package nextstep.subway.path.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.path.domain.Graph;
import nextstep.subway.path.domain.Path;
import nextstep.subway.station.applicaion.StationService;
import nextstep.subway.station.applicaion.dto.response.StationResponse;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PathService {

    private final StationService stationService;
    private final SectionService sectionService;

    public PathResponse findPath(Long source, Long target) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        List<Section> sections = sectionService.findAllSections();

        Path shortestPath = new Graph(sections).findShortestPath(sourceStation.getId(), targetStation.getId());
        List<StationResponse> pathStations = stationService.findByIds(shortestPath.getVertexes());
        return new PathResponse(pathStations, shortestPath.getDistance());
    }
}
