package subway.station.dto;

import subway.station.Station;

public class StationResponse {
    private Long id;
    private String name;

    private StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse ofEntity(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
