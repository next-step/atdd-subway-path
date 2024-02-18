package nextstep.subway.station.service.dto;

import nextstep.subway.station.domain.Station;

import java.util.Objects;

public class AddSectionStationDto {

    private Long stationId;
    private String name;

    private AddSectionStationDto() {
    }

    private AddSectionStationDto(Long stationId, String name) {
        this.stationId = stationId;
        this.name = name;
    }

    public static AddSectionStationDto from(Station station) {
        return new AddSectionStationDto(
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
        AddSectionStationDto that = (AddSectionStationDto) o;
        return Objects.equals(stationId, that.stationId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, name);
    }

}
