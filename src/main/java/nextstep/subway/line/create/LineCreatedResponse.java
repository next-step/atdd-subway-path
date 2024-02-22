package nextstep.subway.line.create;

import nextstep.subway.line.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineCreatedResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<LineCreatedStationResponse> stations;

    public LineCreatedResponse(Long id, String name, String color, List<LineCreatedStationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineCreatedResponse from(Line line) {
        return new LineCreatedResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getAllStations().stream()
                        .map(LineCreatedStationResponse::from)
                        .collect(Collectors.toList())
        );
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

    public List<LineCreatedStationResponse> getStations() {
        return stations;
    }
}
