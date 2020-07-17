package nextstep.subway.path.dto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import nextstep.subway.station.dto.StationResponse;

public class ShortestPathResponse {
    private final List<StationResponse> stationResponses;
    private final Double distance;
    private final Double duration;

    public ShortestPathResponse(List<StationResponse> stationResponses, Double distance, Double duration) {
        this.stationResponses = stationResponses;
        this.distance = distance;
        this.duration = duration;
    }

    public ShortestPathResponse(Long startId, long endId) {
        this.stationResponses = Collections.emptyList();
        this.distance = 0.0;
        this.duration = 0.0;
    }

    public List<StationResponse> getStationResponses() {
        return Optional.ofNullable(stationResponses).orElseThrow(() -> new RuntimeException("not found response"));
    }

    public Double getDistance() {
        return Optional.ofNullable(distance).orElse(0.0);
    }

    public Double getDuration() {
        return Optional.ofNullable(duration).orElse(0.0);
    }
}
