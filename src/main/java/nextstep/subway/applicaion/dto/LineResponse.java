package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;

import java.util.List;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final int distance;
    private final List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, int distance, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getCalcDistance(),
                StationResponse.listOf(line.getAllStations())
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

    public int getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}

