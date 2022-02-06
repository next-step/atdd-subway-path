package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;
    private final List<SectionResponse> sections;

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = convertStationResponse(line.getStations());
        this.sections = convertSectionResponse(line.getSections());
        this.createdDate = line.getCreatedDate();
        this.modifiedDate = line.getModifiedDate();
    }

    private List<SectionResponse> convertSectionResponse(List<Section> sections) {
        List<SectionResponse> sectionResponses = sections.stream()
                .map(section -> new SectionResponse(section))
                .collect(Collectors.toList());
        return sectionResponses;
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

    private List<StationResponse> convertStationResponse(List<Station> stations) {
        return stations.stream().map(station -> new StationResponse(station)).collect(Collectors.toList());
    }

    public List<SectionResponse> getSections() {
        return sections;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}

