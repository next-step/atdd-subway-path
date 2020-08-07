package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineStationResponses;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stationResponses;
    private int distance;
    private int duration;

    public PathResponse() {
    }

    public PathResponse(List<StationResponse> stationResponses, int distance, int duration) {
        this.stationResponses = stationResponses;
        this.distance = distance;
        this.duration = duration;
    }

    public static PathResponse of(List<StationResponse> stationResponses, LineStationResponses lineStationResponses) {
        return new PathResponse(stationResponses, lineStationResponses.getDistances(), lineStationResponses.getDurations());
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
