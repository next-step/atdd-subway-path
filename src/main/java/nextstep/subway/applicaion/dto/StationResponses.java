package nextstep.subway.applicaion.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Station;

public class StationResponses {

    private List<StationResponse> stations;

    public StationResponses(List<StationResponse> stations) {
        this.stations = stations;
    }

    public static StationResponses from(List<Station> stations) {
        return new StationResponses(stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList()));
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
