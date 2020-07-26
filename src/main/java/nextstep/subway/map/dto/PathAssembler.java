package nextstep.subway.map.dto;

import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PathAssembler {
    public static PathResponse toPathResponse(List<LineStation> shortestPath, List<Station> stations) {
        Integer distance = sumDistance(shortestPath);
        Integer duration = sumDuration(shortestPath);

        List<StationResponse> stationResponses = getStationResponses(shortestPath, stations);

        return new PathResponse(stationResponses, distance, duration);
    }

    private static Integer sumDuration(List<LineStation> shortestPath) {
        return shortestPath.stream()
                .mapToInt(it -> it.getDuration())
                .sum();
    }

    private static Integer sumDistance(List<LineStation> shortestPath) {
        return shortestPath.stream()
                .mapToInt(it -> it.getDistance())
                .sum();
    }

    private static List<StationResponse> getStationResponses(List<LineStation> shortestPath, List<Station> stations) {
        return shortestPath.stream()
                .map(it -> getStationResponse(stations, it))
                .collect(Collectors.toList());
    }

    private static StationResponse getStationResponse(List<Station> stations, LineStation it) {
        Optional<Station> station = stations.stream()
                .filter(st -> st.getId() == it.getStationId())
                .findFirst();
        return StationResponse.of(station.orElseThrow(RuntimeException::new));
    }
}
