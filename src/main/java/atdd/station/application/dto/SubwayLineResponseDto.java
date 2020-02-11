package atdd.station.application.dto;

import atdd.station.domain.SubwayLine;

import java.util.Objects;

public class SubwayLineResponseDto {
    private Long id;
    private String name;

    public SubwayLineResponseDto() {
    }

    public SubwayLineResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SubwayLineResponseDto of(SubwayLine subwayLine) {
        return new SubwayLineResponseDto(subwayLine.getId(), subwayLine.getName());
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
        SubwayLineResponseDto that = (SubwayLineResponseDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "SubwayLineResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
