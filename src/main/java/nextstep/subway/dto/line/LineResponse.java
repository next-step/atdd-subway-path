package nextstep.subway.dto.line;

import nextstep.subway.dto.station.StationResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();

    protected LineResponse() {}

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations.addAll(
            createStationResponses(line.getStationList())
        );
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
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
}