package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional(readOnly = true)
    public PathResponse findShortestPath(Long source, Long target) {
        Station sourceStation = stationService.findStationById(source);
        Station targetStation = stationService.findStationById(target);

        List<Line> allLines = lineService.findAllLines();
        List<Section> allSections = getAllSections(allLines);

        Path path = new Path(allSections, sourceStation, targetStation);
        int totalDistance = path.getTotalDistance();
        List<Station> shortestPath = path.findShortestPath();

        return new PathResponse(shortestPath, totalDistance);
    }

    private List<Section> getAllSections(List<Line> allLines) {
        return allLines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
