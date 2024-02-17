package nextstep.subway.station.service.dto;

import nextstep.subway.station.domain.Station;

import java.util.Objects;

public class ShowStationDto {

    private Long stationId;
    private String name;

    private ShowStationDto() {
    }

    private ShowStationDto(Long stationId, String name) {
        this.stationId = stationId;
        this.name = name;
    }

    public static ShowStationDto from(Station station) {
        return new ShowStationDto(
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
        ShowStationDto that = (ShowStationDto) o;
        return Objects.equals(stationId, that.stationId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, name);
    }

}
