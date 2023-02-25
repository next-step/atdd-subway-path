package nextstep.subway.applicaion;

import static java.util.stream.Collectors.*;

import java.util.Collection;
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

        List<Section> allSections = lineService.findAllSections();
        List<Station> allStations = getAllStations(allSections);

        return PathResponse.createResponse(new SubwayMap(allStations, allSections).findShortestPath(sourceStation, targetStation));
    }

    private List<Station> getAllStations(List<Section> sections) {
        return sections.stream()
            .map(section -> List.of(section.getUpStation(), section.getDownStation()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(toList());
    }

}
