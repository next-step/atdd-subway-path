package nextstep.subway.controller.dto;

import nextstep.subway.service.dto.LineDto;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponseBody {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponseBody> stations;

    private LineResponseBody(LineDto lineDto) {
        this.id = lineDto.getId();
        this.name = lineDto.getName();
        this.color = lineDto.getColor();
        this.stations = StationResponseBody.create(lineDto.getStations());
    }

    public static List<LineResponseBody> create (List<LineDto> lineDtoList) {
        return lineDtoList.stream()
                .map(LineResponseBody::new)
                .collect(Collectors.toList());
    }

    public static LineResponseBody create (LineDto lineDto) {
        return new LineResponseBody(lineDto);
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

    public List<StationResponseBody> getStations() {
        return stations;
    }
}
