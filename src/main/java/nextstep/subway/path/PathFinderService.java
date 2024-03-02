package nextstep.subway.path;

import nextstep.subway.line.Section;
import nextstep.subway.line.SectionRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathFinderService {

    private final Route route;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public PathFinderService(Route route, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.route = route;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public PathFoundResponse findPath(Long sourceStationId, Long targetStationId) {
        initRoute();
        Station sourceStation = findStationById(sourceStationId);
        Station targetStation = findStationById(targetStationId);
        List<Station> stations = route.findShortestPath(sourceStation, targetStation);
        int distance = route.findShortestDistance(sourceStation, targetStation);
        return PathFoundResponse.of(stations, distance);
    }

    private void initRoute() {
        List<Section> sections = sectionRepository.findAll();
        route.initGraph(sections);
    }

    private Station findStationById(Long sourceStationId) {
        return stationRepository.findById(sourceStationId)
                .orElseThrow(() -> new IllegalArgumentException("출발역이 존재하지 않습니다. stationId: " + sourceStationId));
    }
}
