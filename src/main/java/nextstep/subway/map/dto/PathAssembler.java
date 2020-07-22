package nextstep.subway.map.dto;

import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PathAssembler {
    public static PathResponse toPathResponse(StationRepository stationRepository, List<LineStation> shortestPath) {
        List<Long> stationIds = shortestPath.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());

        Integer distance = sumDistance(shortestPath);
        Integer duration = sumDuration(shortestPath);

        List<StationResponse> stationResponses = getStationResponses(stationRepository, shortestPath, stationIds);

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

    private static List<StationResponse> getStationResponses(StationRepository stationRepository, List<LineStation> shortestPath, List<Long> stationIds) {
        List<Station> stations = stationRepository.findAllById(stationIds);

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
