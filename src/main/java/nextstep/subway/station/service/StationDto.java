package nextstep.subway.station.service;

import nextstep.subway.station.domain.Station;

public class StationDto {

    private Long stationId;
    private String name;

    public StationDto(Long stationId, String name) {
        this.stationId = stationId;
        this.name = name;
    }

    public static StationDto from(Station station) {
        return new StationDto(
                station.getStationId(),
                station.getName()
        );
    }

    public Long getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
    }

}
