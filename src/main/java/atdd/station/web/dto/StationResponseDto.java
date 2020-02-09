package atdd.station.web.dto;

import java.util.Objects;

public class StationResponseDto {
    private String name;

    public StationResponseDto() {
    }

    public StationResponseDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static StationResponseDto of(String name) {
        return new StationResponseDto(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationResponseDto that = (StationResponseDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
