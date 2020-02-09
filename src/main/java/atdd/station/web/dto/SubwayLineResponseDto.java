package atdd.station.web.dto;

import atdd.station.domain.SubwayLine;

import java.util.Objects;

public class SubwayLineResponseDto {
    private String name;

    public SubwayLineResponseDto() {
    }

    public SubwayLineResponseDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SubwayLineResponseDto of(SubwayLine subwayLine) {
        return new SubwayLineResponseDto(subwayLine.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubwayLineResponseDto that = (SubwayLineResponseDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
