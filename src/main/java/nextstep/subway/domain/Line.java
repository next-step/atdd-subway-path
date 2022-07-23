package nextstep.subway.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.*;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<Section> sections = new HashSet<>();

    public Line() {
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }
    public void setColor(String color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Station downStation() {
        if (this.sections.size() > 0)
            return this.sections.stream().reduce((first, second) -> second).orElseThrow(null).getDownStation();
        return null;
    }

    public Station upStation() {
        if (this.sections.size() > 0)
            return this.sections.stream().findFirst().get().getUpStation();
        return null;
    }

    public Set<Station> stations() {
        Set<Station> stations = new HashSet<>();
        addUpStations(stations);
        addDownStations(stations);
        return stations;
    }

    private void addDownStations(Set<Station> stations) {
        for (Section section : this.sections) {
            stations.add(section.getDownStation());
        }
    }

    private void addUpStations(Set<Station> stations) {
        for (Section section : this.sections) {
            stations.add(section.getUpStation());
        }
    }

    public Set<Section> sections() {
        return this.sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        this.sections.add(section);
    }

    public void deleteSection(Section section) {
        this.sections.remove(section);
    }
}
