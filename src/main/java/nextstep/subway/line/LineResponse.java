package nextstep.subway.line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.section.Section;
import nextstep.subway.station.StationResponse;

public class LineResponse {
    private long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {}

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = getStationsResponse(line);

    }

    private List<StationResponse> getStationsResponse(Line line) {
        List<StationResponse> stations = new ArrayList<>();

        for (Section section : line.getSections()) {
            stations.add(new StationResponse(section.getUpStation()));
            stations.add(new StationResponse(section.getDownStation()));
        }

        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
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

    @Override
    public String toString() {
        return "StationLineResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }
}
