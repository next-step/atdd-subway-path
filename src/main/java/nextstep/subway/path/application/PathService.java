package nextstep.subway.path.application;

import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.common.exception.ApplicationType;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private LineService lineService;
    private StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findPath(Long source, Long target) {
        Station sourceStation = stationService.findStationById(source);
        Station targetStation = stationService.findStationById(target);

        if (sourceStation.equals(targetStation)) {
            throw new ApplicationException(ApplicationType.CANNOT_SAME_WITH_SOURCE_AND_TARGET_ID);
        }
        List<Sections> sections = getAllSectionsContainsSourceAndTargetStation(sourceStation, targetStation);
        Path path = new PathFinder(sections).getShortestPath(sourceStation, targetStation);
        return PathResponse.of(path);
    }

    private List<Sections> getAllSectionsContainsSourceAndTargetStation(Station sourceStation, Station targetStation) {
        return lineService.findAllLines().stream()
                .map(Line::getSections)
                .filter(sections -> sections.getSortedStations().contains(sourceStation) || sections.getSortedStations().contains(targetStation))
                .collect(Collectors.toList());
    }
}
