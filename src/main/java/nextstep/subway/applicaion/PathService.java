package nextstep.subway.applicaion;

import nextstep.subway.domain.SubwayMap;
import nextstep.subway.domain.SubwayPath;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final SectionService sectionService;
    private final StationService stationService;

    public PathService(SectionService sectionService, StationService stationService) {
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    public SubwayPath findPath(Station source, Station target) {

        List<Station> allStations = stationService.findAllStations();
        List<Section> allSections = sectionService.findAllSections();

        SubwayMap subwayMap = SubwayMap.of(allStations, allSections);
        return subwayMap.findShortestPath(source, target);
    }
}


