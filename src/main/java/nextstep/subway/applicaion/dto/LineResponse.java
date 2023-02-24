package nextstep.subway.applicaion.dto;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<SectionResponse> sections;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<SectionResponse> sections, List<StationResponse> stations) {
        System.out.println(sections);
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    public List<StationResponse> getStations() {
        return stations;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }
}

