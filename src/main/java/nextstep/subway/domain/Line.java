package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection() {
        int lastSectionIndex = sections.size() - 1;
        if (lastSectionIndex > 0) {
            sections.remove(lastSectionIndex);
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        stations.add(sections.get(0).getUpStation());
        for (Section section: sections) {
            stations.add(section.getDownStation());
        }

        return stations;
    }


    public List<StationResponse> createStationResponses() {
        if (getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, getSections().get(0).getUpStation());

        return stations.stream()
                .map(Station::createStationResponse)
                .collect(Collectors.toList());
    }

    public LineResponse createLineResponse() {
        return new LineResponse(
                getId(),
                getName(),
                getColor(),
                createStationResponses()
        );
    }




}
