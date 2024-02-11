package nextstep.subway.line.service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.service.StationDto;

import java.util.List;
import java.util.stream.Collectors;

public class LineDto {

    private Long lineId;

    private String name;

    private String color;

    private List<StationDto> stations;

    private LineDto() {
    }

    private LineDto(Long lineId, String name, String color, List<StationDto> stations) {
        this.lineId = lineId;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineDto from(Line line) {
        return new LineDto(
                line.getLineId(),
                line.getName(),
                line.getColor(),
                line.getStations().stream()
                        .distinct()
                        .map(StationDto::from)
                        .collect(Collectors.toList())
        );
    }

    public Long getLineId() {
        return lineId;
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

}
