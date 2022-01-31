package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private Set<StationResponse> stations = new HashSet<>();
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private LineResponse(Long id, String name, String color, Set<StationResponse> sections, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = sections;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        Sections sections = line.getSections();


        if (sections.getSections().isEmpty()) {
            return new LineResponse(line.getId(), line.getName(), line.getColor(), Collections.emptySet(), line.getCreatedDate(), line.getModifiedDate());
        }
        Set<StationResponse> result = distinctDuplication(sections);

        return new LineResponse(line.getId(), line.getName(), line.getColor(), result, line.getCreatedDate(), line.getModifiedDate());
    }


    private static Set<StationResponse> distinctDuplication(Sections sections) {
        Set<StationResponse> result = new LinkedHashSet<>();
        Section firstSection = sections.findFirstSection();
        result.add(StationResponse.of(firstSection.getUpStation()));
        result.add(StationResponse.of(firstSection.getDownStation()));

        while (true) {
            try {
                Section section = sections.findSectionByUpStation(firstSection.getDownStation().getId());
                result.add(StationResponse.of(firstSection.getUpStation()));
                result.add(StationResponse.of(firstSection.getDownStation()));
                firstSection = section;
            } catch (Exception e) {
                result.add(StationResponse.of(firstSection.getDownStation()));
                break;
            }
        }
        return result;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<StationResponse> getStations() {
        return stations;
    }

    public String getColor() {
        return color;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

}
