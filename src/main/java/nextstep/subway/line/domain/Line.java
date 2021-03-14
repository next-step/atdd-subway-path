package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.*;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Station firstStation = sections.get(0).getUpStation();
        stations.add(firstStation);

        sections.stream().forEach(section -> {
            stations.add(section.getDownStation());
        });

        return stations;
    }

    public void addSection(Section section) {
        checkIfSectionIsValid(section);
        sections.add(section);
    }

    private void checkIfSectionIsValid(Section section) {
        if(sections.isEmpty()) {
            return;
        }

        checkIfUpStationValid(section);
        checkIfStationAlreadyExists(section);
    }

    private void checkIfUpStationValid(Section section) {
        Station finalStation = sections.get(sections.size() - 1).getDownStation();
        if(!finalStation.equals(section.getUpStation())) {
            throw new FinalStationNeededException();
        }

    }

    private void checkIfStationAlreadyExists(Section section) {
        boolean isAlreadyExists = sections.stream().anyMatch(s -> section.getDownStation().equals(s.getUpStation()));

        if(isAlreadyExists) {
            throw new StationAlreadyExistsException();
        }
    }

    public void removeSection(Long id) {
        if(sections.size() <= 1) {
            throw new OnlyOneSectionRemainingException();
        }

        Section targetSection = sections.stream()
                .filter(section -> section.getDownStation().getId().equals(id))
                .findAny()
                .orElseThrow(NoStationToRemoveException::new);

        sections.remove(targetSection);
    }
}
