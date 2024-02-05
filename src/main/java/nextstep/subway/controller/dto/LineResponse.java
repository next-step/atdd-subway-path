package nextstep.subway.controller.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

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

    public static List<LineResponse> listOf(List<Line> lines, List<Section> sections) {
        return lines.stream()
                .map(line -> ofWithSections(line, sections))
                .collect(Collectors.toList());
    }

    public static LineResponse ofWithStations(Line line, List<Station> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), StationResponse.listOf(stations));
    }

    public static LineResponse ofWithSections(Line line, List<Section> sections) {
        List<Station> containStations = containStationWithLine(line, sections);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), StationResponse.listOf(containStations));
    }

    private static List<Station> containStationWithLine(Line line, List<Section> sections) {
        return sections.stream()
                .filter(section -> section.isSameLine(line))
                .map(Section::stations)
                .flatMap(List::stream)
                .distinct()
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
