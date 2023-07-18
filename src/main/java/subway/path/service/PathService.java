package subway.path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import subway.line.model.Line;
import subway.line.model.Section;
import subway.line.service.LineService;
import subway.path.component.PathFinder;
import subway.path.dto.PathRetrieveResponse;
import subway.station.model.Station;
import subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PathService {

    private final StationService stationService;
    private final LineService lineService;
    private final PathFinder pathFinder;

    public PathRetrieveResponse getShortestPath(long sourceStationId, long targetStationId) {
        Station sourceStation = stationService.findStationById(sourceStationId);
        Station targetStation = stationService.findStationById(targetStationId);
        List<Line> lines = lineService.findByStation(sourceStation, targetStation);
        List<Section> sections = getAllSections(lines);
        return pathFinder.findShortestPath(sections, sourceStation, targetStation);
    }

    private List<Section> getAllSections(List<Line> lines) {
        return lines.stream()
                .flatMap(line -> line.getLineSections().getSections().stream())
                .collect(Collectors.toList());
    }


}
