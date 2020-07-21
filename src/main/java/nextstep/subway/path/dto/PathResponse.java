package nextstep.subway.path.dto;

import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathResponse {

    private List<StationResponse> stations;
    private Integer distance;
    private Integer duration;

    public PathResponse() {}

    public PathResponse(List<LineStation> lineStations, List<Station> stations) {
        Map<Long, Station> stationById = stations.stream()
                .collect(Collectors.toMap(Station::getId, Function.identity()));

        this.stations = lineStations.stream()
                .map(LineStation::getStationId)
                .map(stationById::get)
                .map(StationResponse::of)
                .collect(Collectors.toList());

        this.distance = lineStations.stream()
                .mapToInt(LineStation::getDistance)
                .sum();

        this.duration = lineStations.stream()
                .mapToInt(LineStation::getDuration)
                .sum();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }
}
