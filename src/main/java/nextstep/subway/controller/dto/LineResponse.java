package nextstep.subway.controller.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

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

    public static LineResponse ofWithStations(Line line, List<Station> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), StationResponse.listOf(stations));
    }

    public static LineResponse ofWithSections(Line line, List<StationResponse> stationResponses) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
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
