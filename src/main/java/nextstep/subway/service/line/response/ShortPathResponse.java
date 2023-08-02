package nextstep.subway.service.line.response;

import nextstep.subway.domain.line.ShortPath;
import nextstep.subway.service.station.response.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShortPathResponse {

    private List<StationResponse> stations;
    private int distance;

    private ShortPathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static ShortPathResponse of(ShortPath shortPath) {
        return new ShortPathResponse(
                shortPath.getStations().stream().map(StationResponse::of).collect(Collectors.toList()),
                shortPath.getDistance()
        );
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
