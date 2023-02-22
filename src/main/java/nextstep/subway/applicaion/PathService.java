package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

    private final StationService stationService;
    private final SectionService sectionService;

    public PathService(StationService stationService, SectionService sectionService) {
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public PathResponse getPaths(Long source, Long target) {
        List<Station> allStations = stationService.findAllStations();
        List<Section> allSections = sectionService.findAllSections();

        Path path = new Path(allStations, allSections);

        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        return path.findPath(sourceStation, targetStation);
    }

}
