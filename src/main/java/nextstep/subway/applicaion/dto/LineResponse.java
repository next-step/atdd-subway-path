package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.Objects;

import static nextstep.subway.applicaion.dto.StationResponse.createStationResponses;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int distance;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, int distance, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getDistance(),
                createStationResponses(line)
        );
    }

    public static LineResponse from(Line line, List<Station> stations) {
        LineResponse lineResponse = new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getDistance(),
                createStationResponses(stations)
        );
        return lineResponse;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineResponse that = (LineResponse) o;
        return distance == that.distance && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(stations, that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, distance, stations);
    }
}

