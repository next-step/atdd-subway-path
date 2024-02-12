package nextstep.subway.service;

import nextstep.subway.controller.dto.PathResponse;
import nextstep.subway.controller.dto.StationResponse;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.ShortestPathType;
import nextstep.subway.domain.Station;
import nextstep.subway.factory.ShortestPathFactory;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.strategy.ShortestPathStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public PathService(StationRepository stationRepository, SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public PathResponse findPaths(Long sourceId, Long targetId) {
        Station source = stationRepository.getBy(sourceId);
        Station target = stationRepository.getBy(targetId);

        List<Section> sections = sectionRepository.findAll();
        ShortestPathStrategy shortestPathStrategy = new ShortestPathFactory().generateStrategy(ShortestPathType.DIJKSTRA, sections);

        Path path = new Path(shortestPathStrategy);
        List<Station> stations = path.findShortestPath(source, target);
        int distance = path.findShortestDistance(source, target);

        return new PathResponse(convertToStationResponse(stations), distance);
    }

    private List<StationResponse> convertToStationResponse(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }
}
