package nextstep.subway.ui.response;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private final List<StationResponse> stations;
    private final double distance;

    public PathResponse(List<StationResponse> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse from(List<Station> stations, double weight) {
        return new PathResponse(
                stations.stream()
                        .map(it -> new StationResponse(it.getId(), it.getName()))
                        .collect(Collectors.toList()),
                weight
        );
    }

    public static PathResponse from(List<Long> vertexList, List<Station> stations, double weight) {
        List<Station> orderedStations = vertexList.stream()
                .map(it -> stations.stream().filter(station -> station.getId().equals(it)).findFirst().get())
                .collect(Collectors.toList());
        return PathResponse.from(orderedStations, weight);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}
