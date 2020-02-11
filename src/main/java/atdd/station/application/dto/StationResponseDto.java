package atdd.station.application.dto;

import atdd.station.domain.Station;

import java.util.List;
import java.util.Objects;

public class StationResponseDto {
    private Long id;
    private String name;
    private List<SubwayCommonResponseDto> lines;

    public StationResponseDto() {
    }

    private StationResponseDto(Long id, String name, List<SubwayCommonResponseDto> lines) {
        this.id = id;
        this.name = name;
        this.lines = lines;
    }

    public static StationResponseDto of(Station station, List<SubwayCommonResponseDto> lines) {
        return new StationResponseDto(station.getId(), station.getName(), lines);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SubwayCommonResponseDto> getLines() {
        return lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationResponseDto that = (StationResponseDto) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(lines, that.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lines);
    }

    @Override
    public String toString() {
        return "StationResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lines=" + lines +
                '}';
    }
}
