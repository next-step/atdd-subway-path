package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    private PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse from(GraphPath path) {
        List<Station> stations = path.getVertexList();
        return new PathResponse(stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList()), (int) path.getWeight());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
