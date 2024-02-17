package nextstep.subway.line.dto;

import nextstep.subway.line.entity.Section;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.line.entity.Line;
import nextstep.subway.station.entity.Station;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public static LineResponse convertToDto(final Line line) {
        final List<Section> sections = line.getSections().getSections();

        if (sections.isEmpty()) {
            return new LineResponse(line.getId(), line.getName(), line.getColor());
        }

        final Set<Station> stationsOrdered = new LinkedHashSet<>();

        for (Section section : sections) {
            stationsOrdered.add(section.getUpStation());
            stationsOrdered.add(section.getDownStation());
        }

        final List<StationResponse> stations = stationsOrdered.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

}
