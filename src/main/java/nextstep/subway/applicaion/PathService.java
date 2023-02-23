package nextstep.subway.applicaion;

import java.util.List;

import org.springframework.stereotype.Service;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayMap;

@Service
public class PathService {
    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse findShortestPath(Long source, Long target) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        List<Station> allStations = stationService.findAllStations();
        List<Section> allSections = lineService.findAllSections();

        return PathResponse.createResponse(new SubwayMap(allStations, allSections).findShortestPath(sourceStation, targetStation));
    }
}
