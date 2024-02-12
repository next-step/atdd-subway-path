package nextstep.subway.station.service;

import nextstep.subway.station.domain.Station;

import java.util.Objects;

public class StationDto {

    private Long stationId;
    private String name;

    private StationDto(){
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationDto that = (StationDto) o;
        return Objects.equals(stationId, that.stationId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, name);
    }

}
