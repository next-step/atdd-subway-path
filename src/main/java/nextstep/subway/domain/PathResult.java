package nextstep.subway.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.exception.StationNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class PathResult {
    private final List<Station> pathStations;
    private final int totalDistance;

    public List<Station> getSortedStationsInPathOrder(List<Station> allStations) {
        return pathStations.stream()
                .map(pathStation -> allStations.stream()
                        .filter(station -> station.equals(pathStation))
                        .findFirst()
                        .orElseThrow(() -> new StationNotFoundException(pathStation.getId())))
                .collect(Collectors.toList());
    }
}
