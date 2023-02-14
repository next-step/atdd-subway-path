package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.SubwayPath;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    private PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(SubwayPath subwayPath){
        List<StationResponse> stationResponses = subwayPath.getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toUnmodifiableList());
        return new PathResponse(stationResponses, subwayPath.getDistance());

    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
