package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Station station) {
        id = station.getId();
        name = station.getName();
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
