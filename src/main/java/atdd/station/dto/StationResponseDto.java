package atdd.station.dto;

import java.util.List;
import java.util.Objects;

public class StationResponseDto {

    private Long id;
    private String name;
    private List<LineDto> lines;

    private StationResponseDto() { }

    public StationResponseDto(Long id, String name, List<LineDto> lines) {
        this.id = id;
        this.name = name;
        this.lines = lines;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<LineDto> getLines() {
        return lines;
    }

    public static class LineDto {
        private Long id;
        private String name;

        private LineDto() { }

        public static LineDto of(Long id, String name) {
            LineDto lineDto = new LineDto();
            lineDto.id = id;
            lineDto.name = name;
            return lineDto;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    @Override
    public String toString() {
        return "StationResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StationResponseDto)) return false;
        StationResponseDto that = (StationResponseDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
