package atdd.station.application.dto;

import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;

import java.util.Objects;

public class SubwayCommonResponseDto {
    protected Long id;
    protected String name;

    private SubwayCommonResponseDto() {
    }

    public SubwayCommonResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SubwayCommonResponseDto of(SubwayLine subwayLine) {
        return new SubwayCommonResponseDto(subwayLine.getId(), subwayLine.getName());
    }

    public static SubwayCommonResponseDto of(Station station) {
        return new SubwayCommonResponseDto(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubwayCommonResponseDto that = (SubwayCommonResponseDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
