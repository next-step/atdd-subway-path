package nextstep.subway.application.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int distance;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(final Line savedLine) {
        this.id = savedLine.getId();
        this.name = savedLine.getName();
        this.color = savedLine.getColor();
        this.distance = savedLine.totalDistance();
        this.stations =  savedLine.getStations().stream()
                .map(this::createStationResponse)
                .collect(Collectors.toList());
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

    public int getDistance() {
        return distance;
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
