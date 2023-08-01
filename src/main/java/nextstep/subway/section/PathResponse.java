package nextstep.subway.section;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationResponse;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public PathResponse() {}

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public static PathResponse of(List<Station> stations, int distance) {
        List<StationResponse> stationResponses = stations.stream()
            .map(StationResponse::new)
            .collect(Collectors.toList());

        return new PathResponse(stationResponses, distance);
    }
}
