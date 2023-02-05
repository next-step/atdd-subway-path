package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

public class StationResponse {
    private Long id;
    private String name;

    public static StationResponse createResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
