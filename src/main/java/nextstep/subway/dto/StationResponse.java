package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.domain.Station;

public class StationResponse {

    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse from(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public static List<StationResponse> from(List<Station> stations) {
        return stations.stream()
            .map(StationResponse::from)
            .collect(Collectors.toUnmodifiableList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
