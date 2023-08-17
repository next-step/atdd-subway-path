package subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import subway.domain.Path;
import subway.domain.Station;
import subway.domain.SubwayMap;
import subway.dto.PathRequest;

@Service
@RequiredArgsConstructor
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public Path findPath(PathRequest request) {
        Station source = stationService.findStationById(request.getSource());
        Station target = stationService.findStationById(request.getTarget());
        SubwayMap subwayMap = new SubwayMap(lineService.findAllLines());

        return subwayMap.findPath(source, target);
    }

}
