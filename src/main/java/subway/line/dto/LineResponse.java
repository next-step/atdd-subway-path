package subway.line.dto;

import subway.line.entity.Line;
import subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private Long id;

    private String name;

    private String color;

    private List<StationResponse> stations;

    private LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse ofEntity(Line line) {
        List<StationResponse> stations = line.getSections().getStations().stream()
                .map(StationResponse::ofEntity).collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
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
