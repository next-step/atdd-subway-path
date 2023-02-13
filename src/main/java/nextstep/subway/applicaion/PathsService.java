package nextstep.subway.applicaion;

import nextstep.subway.domain.Maps;
import nextstep.subway.domain.Paths;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathsService {

    private final SectionService sectionService;
    private final StationService stationService;

    public PathsService(SectionService sectionService, StationService stationService) {
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public Paths findPath(Station source, Station target) {
        List<Station> allStations = stationService.findAllStations();
        List<Section> allSections = sectionService.findAllSections();

        Maps maps = Maps.of(allStations, allSections);

        return maps.findShortestPath(source, target);
    }
}


