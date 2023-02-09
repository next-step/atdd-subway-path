package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.exception.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

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
        return sections.getSections();
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            if (isNotDownStationId(section.getUpStation().getId())) {
                throw new SectionUpStationNotMatchException();
            }
            if (doesNotContainStationId(section.getDownStation().getId())) {
                throw new SectionAlreadyCreateStationException();
            }
        }
        sections.add(section);
    }

    private boolean doesNotContainStationId(Long id) {
        List<Station> stations = getStations();
        return stations.stream()
                .map(Station::getId)
                .anyMatch((stationId) -> stationId.equals(id));
    }

    private boolean isNotDownStationId(long id) {
        return !sections.getLastSection().getDownStation().getId().equals(id);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Station station) {
        if (!sections.getLastSection().getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.removeLastSection();
    }

    public void changeNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public boolean isEmptySection() {
        return this.sections.isEmpty();
    }

    public boolean checkExistStation(Station station) {
        return getStations().stream()
                .anyMatch(s -> s.equals(station));
    }
}
