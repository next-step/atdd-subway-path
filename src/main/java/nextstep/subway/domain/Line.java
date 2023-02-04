package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;

import javax.persistence.*;
import java.util.ArrayList;
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

    public Line(String name, String color, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    public List<Station> getStations() {
        if(sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> stations = this.sections.stream()
                                            .map(Section::getUpStation)
                                            .collect(Collectors.toList());
        Station lastStation = this.sections.stream()
                                            .reduce((first, second) -> second)
                                            .map(Section::getDownStation)
                                            .orElseThrow(RuntimeException::new);
        stations.add(lastStation);

        return stations;
    }

    public void addSection(Section section) {
        if(this.sections.isEmpty()) {
            sections.add(section);
            return;
        }

        Station lastStation = this.sections.stream()
                                            .reduce((first, second) -> second)
                                            .map(Section::getDownStation)
                                            .orElseThrow(RuntimeException::new);

        if(lastStation.getId() != section.getUpStation().getId()) {
            throw new CustomException(CustomException.ONLY_CAN_CREATE_LAST_STATION_MSG);
        }

        if(getStations().stream().map(Station::getId).anyMatch(stationId -> stationId.equals(section.getDownStation().getId()))) {
            throw new CustomException(CustomException.DUPLICATE_STATION_MSG);
        }

        sections.add(section);
        section.setLine(this);
    }

    public void removeSection(Station station) {
        if(sections.isEmpty()) {
            throw new RuntimeException();
        }

        Section lastSection = sections.get(sections.size() - 1);
        if(station.getId() != lastSection.getDownStation().getId()) {
            throw new CustomException(CustomException.ONLY_CAN_REMOVE_LAST_STATION_MSG);
        }

        sections.remove(sections.size() - 1);
    }
}
