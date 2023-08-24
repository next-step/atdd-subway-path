package subway.application.mapper;

import org.springframework.stereotype.Component;
import subway.application.response.PathResponse;
import subway.domain.PathSearcher;
import subway.domain.PathStation;
import subway.domain.PathSubway;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PathResponseMapper {
    public PathResponse from(PathSubway path) {
        return PathResponse.of(from(path.getStations()), path.getDistance());
    }

    private List<PathResponse.Station> from(List<PathStation> stations) {
        return stations
                .stream()
                .map(station -> PathResponse.Station.of(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }
}
