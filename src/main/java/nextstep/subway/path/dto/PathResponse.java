package nextstep.subway.path.dto;

import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private Integer distance;
    private Integer duration;

    public PathResponse(List<StationResponse> stations, Integer distance, Integer duration) {
        this.stations = stations;
        this.distance = distance;
        this.duration = duration;
    }

    public PathResponse() {
    }

    public static PathResponse of(List<StationResponse> stations, Integer distance, Integer duration) {
        return new PathResponse(stations, distance, duration);
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
