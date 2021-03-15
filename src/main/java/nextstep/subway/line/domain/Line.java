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

        boolean isUpStationExists = isUpStationExists(section);
        boolean isDownStationExists = isDownStationExists(section);

        if(isUpStationExists && isDownStationExists) {
            throw new BothStationExistsException();
        }

        if(section.getDownStation().equals(getFirstStation())){
            sections.add(0, section);
            return;
        }

        if(isUpStationExists) {
            Section oldSection = sections.stream()
                    .filter(s -> s.getUpStation().equals(section.getUpStation()))
                    .findAny()
                    .get();

            Station oldDownStation = oldSection.getDownStation();
            int oldDistance = oldSection.getDistance();

            checkIfDistanceValid(section, oldDistance);


            oldSection.changeDownStationAndDistance(section.getDownStation(), section.getDistance());
            addSection(new Section(this, section.getDownStation(), oldDownStation,oldDistance - section.getDistance()));
            return;
        }

        if(isDownStationExists) {
            Section oldSection = sections.stream()
                    .filter(s -> s.getDownStation().equals(section.getDownStation()))
                    .findAny()
                    .get();

            Station oldDownStation = oldSection.getDownStation();
            int oldDistance = oldSection.getDistance();

            checkIfDistanceValid(section, oldDistance);

            oldSection.changeDownStationAndDistance(section.getUpStation(), oldDistance - section.getDistance());
            addSection(new Section(this, section.getUpStation(), oldDownStation, section.getDistance()));
            return;
        }

        sections.add(section);
    }

    private Station getFirstStation() {
        return sections.get(0).getUpStation();
    }

    private void checkIfDistanceValid(Section section, int oldDistance) {
        if (oldDistance <= section.getDistance()) {
            throw new InvalidDistanceException();
        }
    }

    private boolean isUpStationExists(Section section) {
        return sections.stream()
                .anyMatch(s -> s.getUpStation().equals(section.getUpStation()));
    }

    private boolean isDownStationExists(Section section) {
        return sections.stream()
                .anyMatch(s -> s.getDownStation().equals(section.getDownStation()));
    }

    private void checkIfSectionIsValid(Section section) {
        if(sections.isEmpty()) {
            return;
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
