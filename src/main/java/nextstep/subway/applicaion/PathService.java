package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Path;
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
        List<StationResponse> allStations = stationService.findAllStations();
        List<SectionResponse> allSections = sectionService.findAllSections();

        Path path = new Path(allStations, allSections);

        StationResponse sourceStation = loadStationResponse(source);
        StationResponse targetStation = loadStationResponse(target);

        return path.findPath(sourceStation, targetStation);
    }

    private StationResponse loadStationResponse(Long source) {
        Station sourceStation = stationService.findById(source);
        return stationService.createStationResponse(sourceStation);
    }

}
