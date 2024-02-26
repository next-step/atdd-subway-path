package nextstep.subway.path;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationResponse;

public class PathResponse {

    private final List<StationResponse> stations;
    private final Integer distance;

    private PathResponse(List<StationResponse> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse createResponse(List<Station> stations, Double distance) {
        ArrayList<StationResponse> response = new ArrayList<>();
        for (Station station : stations) {
            response.add(new StationResponse(station.getId(), station.getName()));
        }
        return new PathResponse(response, distance.intValue());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
