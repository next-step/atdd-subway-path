package nextstep.subway.path.dto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import nextstep.subway.station.dto.StationResponse;

public class PathResponse {
    private final List<StationResponse> stationResponses;
    private final Integer distance;
    private final Integer duration;

    public PathResponse(List<StationResponse> stationResponses, Integer distance, Integer duration) {
        this.stationResponses = stationResponses;
        this.distance = distance;
        this.duration = duration;
    }

    private PathResponse() {
        this.stationResponses = Collections.emptyList();
        this.distance = 0;
        this.duration = 0;
    }

    public static PathResponse empty() {
        return new PathResponse();
    }

    public static PathResponse of(ShortestPathResult pathResponse) {
        return new PathResponse(pathResponse.getStations(), pathResponse.getDistance(), pathResponse.getDuration());
    }

    public List<StationResponse> getStationResponses() {
        return Optional.ofNullable(stationResponses).orElseThrow(() -> new RuntimeException("not found response"));
    }

    public Integer getDistance() {
        return Optional.ofNullable(distance).orElse(0);
    }

    public Integer getDuration() {
        return Optional.ofNullable(duration).orElse(0);
    }
}
