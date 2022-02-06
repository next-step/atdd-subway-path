package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class ExploreResponse {
    private List<StationResponse> stations;

    public ExploreResponse() {

    }

    private ExploreResponse(List<StationResponse> stations) {
        this.stations = stations;
    }

    public ExploreResponse from(List<Station> stations) {
        return new ExploreResponse(stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList()));
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
