package nextstep.subway.service.dto;

import nextstep.subway.domain.Line;

import java.util.List;

public class LineDto {
    private Long id;
    private String name;
    private String color;
    private List<StationDto> stations;

    public LineDto(Long id, String name, String color, List<StationDto> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationDto> getStations() {
        return stations;
    }

    public static LineDto from(Line line) {
        return new LineDto(line.getId(), line.getName(), line.getColor(), StationDto.from(line.getAllStations()));
    }

    public static Line toEntity(LineDto lineDto) {
        return Line.create(lineDto.getName(), lineDto.getColor());
    }
}
