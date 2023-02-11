package nextstep.subway.domain;

import nextstep.subway.exception.BothSectionStationsNotExistsInLineException;
import nextstep.subway.exception.SectionStationsAlreadyExistsInLineException;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

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

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        validateSection(section);
        sections.add(section);
    }

    private void validateSection(Section section) {
        if (section.hasIdenticalStations()) {
            throw new IllegalArgumentException();
        }

        List<Station> stations = getStations();
        if (stations.isEmpty()) {
            return;
        }

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new SectionStationsAlreadyExistsInLineException(upStation.getName(), downStation.getName());
        }

        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new BothSectionStationsNotExistsInLineException(upStation.getName(), downStation.getName());
        }
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        return getStationsInOrder();
    }

    private List<Station> getStationsInOrder() {
        return sections.getStationsInOrder();
    }

    public void removeSection(Station station) {
        sections.remove(station);
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
}
