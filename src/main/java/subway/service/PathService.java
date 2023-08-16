package subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import subway.domain.Path;
import subway.domain.Station;
import subway.domain.SubwayMap;

@Service
@RequiredArgsConstructor
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public Path findPath(Long sourceId, Long targetId) {
        Station source = stationService.findStationById(sourceId);
        Station target = stationService.findStationById(targetId);
        SubwayMap subwayMap = new SubwayMap(lineService.findAllLines());

        return subwayMap.findPath(source, target);
    }

}
