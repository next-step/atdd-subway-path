package nextstep.subway.controller.resonse;

import java.util.List;

public class LineResponse {

    private long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private long distance;

    private LineResponse() {
    }

    public LineResponse(long id, String name, String color, List<StationResponse> stations, long distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.distance = distance;
    }

    public long getId() {
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

    public long getDistance() {
        return distance;
    }
}
