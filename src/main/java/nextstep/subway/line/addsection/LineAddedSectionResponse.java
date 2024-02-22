package nextstep.subway.line.addsection;

import java.util.List;

public class LineAddedSectionResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<LineAddedSectionStationResponse> stations;

    public LineAddedSectionResponse(Long id, String name, String color, List<LineAddedSectionStationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
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

    public List<LineAddedSectionStationResponse> getStations() {
        return stations;
    }
}
