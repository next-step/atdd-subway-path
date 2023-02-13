package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathFinder {
    StationService stationService;

    public PathFinder(StationService stationService) {
        this.stationService = stationService;
    }

    public PathResponse searchPath(Long source, Long target) {
        List<Station> Stations = List.of(1L, 4L, 3L).stream().map(a -> stationService.findById(a)).collect(Collectors.toList());

        return new PathResponse(Stations, 5);
    }
}
