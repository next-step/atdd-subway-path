package atdd.station.application.dto;

import atdd.station.domain.SubwayLine;

import java.util.List;
import java.util.Objects;

public class SubwayLineResponseDto {
    private Long id;
    private String name;
    private List<SubwayCommonResponseDto> stations;

    public SubwayLineResponseDto() {
    }

    private SubwayLineResponseDto(Long id, String name, List<SubwayCommonResponseDto> stations) {
        this.id = id;
        this.name = name;
        this.stations = stations;
    }

    public static SubwayLineResponseDto of(SubwayLine subwayLine, List<SubwayCommonResponseDto> stations) {
        return new SubwayLineResponseDto(subwayLine.getId(), subwayLine.getName(), stations);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SubwayCommonResponseDto> getStations() {
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubwayLineResponseDto that = (SubwayLineResponseDto) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(stations, that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, stations);
    }

    @Override
    public String toString() {
        return "SubwayLineResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stations=" + stations +
                '}';
    }
}
