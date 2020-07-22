package nextstep.subway.path.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.dto.StationResponse;

public class ShortestPathResult {

    private final List<StationResponse> stations;
    private final Double weight;

    private ShortestPathResult() {
        this.stations = Collections.emptyList();
        this.weight = 0.0;
    }

    private ShortestPathResult(List<StationResponse> stationResponse, Double weight) {
        this.stations = stationResponse;
        this.weight = weight;
    }

    public static ShortestPathResult ofResult(List<StationResponse> orderedStationResponse, Double weight) {
        if (Objects.isNull(orderedStationResponse)) {
            return ShortestPathResult.empty();
        }
        return new ShortestPathResult(orderedStationResponse, weight);
    }

    public static ShortestPathResult empty() {
        return new ShortestPathResult();
    }

    public List<StationResponse> getStations() {
        return Collections.unmodifiableList(stations);
    }

    public Double getWeight() {
        return weight;
    }

    public List<LineStationResponse> toLineStationResponses(List<LineResponse> allLines) {
        return stations.stream()
            .flatMap(stationResponse ->
                allLines.stream()
                    .flatMap(lineResponse -> lineResponse.getStations().stream())
                    .filter(lineStationResponse -> lineStationResponse.isMatchedStationResponse(stationResponse))
            )
            .collect(Collectors.toList());
    }
}
