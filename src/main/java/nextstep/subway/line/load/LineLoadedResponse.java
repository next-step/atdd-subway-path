package nextstep.subway.line.load;

import nextstep.subway.line.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineLoadedResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<LineLoadedStationResponse> stations;

    public LineLoadedResponse(Long id, String name, String color, List<LineLoadedStationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineLoadedResponse from(Line line) {
        return new LineLoadedResponse(line.getId(), line.getName(), line.getColor(),
                line.getAllStations().stream()
                        .map(LineLoadedStationResponse::from)
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

    public List<LineLoadedStationResponse> getStations() {
        return stations;
    }
}
