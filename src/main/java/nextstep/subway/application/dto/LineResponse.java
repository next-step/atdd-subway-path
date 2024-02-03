package nextstep.subway.application.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private final List<StationResponse> stations = new ArrayList<>();

    public LineResponse() {
    }

    public LineResponse(final Line savedLine) {
        this.id = savedLine.getId();
        this.name = savedLine.getName();
        this.color = savedLine.getColor();

        final List<Section> sections = savedLine.getSections();
        this.stations.add(createStationResponse(sections.get(0).getUpStation()));

        for (Section section : savedLine.getSections()) {
            this.stations.add(createStationResponse(section.getDownStation()));
        }
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

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
