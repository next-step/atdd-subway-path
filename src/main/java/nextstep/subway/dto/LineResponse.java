package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static List<LineResponse> from(List<Line> lines) {
        return lines.stream()
            .map(LineResponse::from)
            .collect(Collectors.toUnmodifiableList());
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
            line.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toUnmodifiableList()));
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
