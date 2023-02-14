package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int totalDistance;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, int totalDistance, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.totalDistance = totalDistance;
        this.stations = stations;
    }

    public static LineResponse toResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getLineDistance(),
                line.getStations().stream()
                        .map(StationResponse::toResponse)
                        .collect(Collectors.toList()));
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

    public int getTotalDistance() {
        return totalDistance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}

