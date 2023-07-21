package nextstep.subway.line.dto;

import nextstep.subway.station.domain.Station;

public class StationOnLineResponse {
    private final Long id;
    private final String name;

    public static StationOnLineResponse of(Station station) {
        return new StationOnLineResponse(station.getId(), station.getName());
    }

    public StationOnLineResponse(Long id, String name) {
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
