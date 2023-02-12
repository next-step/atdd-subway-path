package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

public class StationResponse {
    private Long id;
    private String name;

    private StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse toResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
