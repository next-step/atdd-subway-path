package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateToAddSectionStationsAlreadyAdded(section);
        validateToAddSectionStationNone(section);

        if (addSectionBetween(section)) return;
        if (addSectionToUpStation(section)) return;
        if (addSectionToDownStation(section)) return;
    }

    private boolean addSectionBetween(Section section) {
        if (addSectionBetweenToUpStation(section)) return true;
        if (addSectionBetweenToDownStation(section)) return true;

        return false;
    }

    private boolean addSectionBetweenToUpStation(Section section) {
        Section toBeAfterSection = sections.stream()
                .filter(it -> it.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElse(null);

        if (toBeAfterSection != null) {
            validateToAddSectionDistance(toBeAfterSection, section);

            int position = sections.indexOf(toBeAfterSection);

            toBeAfterSection.setUpStation(section.getDownStation());
            toBeAfterSection.setDistance(toBeAfterSection.getDistance() - section.getDistance());

            sections.set(position, toBeAfterSection);
            sections.add(position, section);

            return true;
        }

        return false;
    }

    private boolean addSectionBetweenToDownStation(Section section) {
        Section toBeBeforeSection = sections.stream()
                .filter(it -> it.getDownStation().equals(section.getDownStation()))
                .findFirst()
                .orElse(null);

        if (toBeBeforeSection != null) {
            validateToAddSectionDistance(toBeBeforeSection, section);

            int position = sections.indexOf(toBeBeforeSection);

            toBeBeforeSection.setDownStation(section.getUpStation());
            toBeBeforeSection.setDistance(toBeBeforeSection.getDistance() - section.getDistance());

            sections.set(position, toBeBeforeSection);
            sections.add(position + 1, section);

            return true;
        }

        return false;
    }

    private boolean addSectionToUpStation(Section section) {
        Section firstSection = sections.get(0);

        if (firstSection.getUpStation().equals(section.getDownStation())) {
            sections.add(0, section);
            return true;
        }

        return false;
    }

    private boolean addSectionToDownStation(Section section) {
        Section lastSection = sections.get(sections.size() - 1);

        if (lastSection.getDownStation().equals(section.getUpStation())) {
            sections.add(section);
            return true;
        }

        return false;
    }

    private void validateToAddSectionDistance(Section section, Section toAddSection) {
        if (toAddSection.getDistance() >= section.getDistance()) {
            throw new RuntimeException();
        }
    }

    private void validateToAddSectionStationsAlreadyAdded(Section toAddSection) {
        if (getStations().containsAll(toAddSection.getStations())) {
            throw new RuntimeException();
        }
    }

    private void validateToAddSectionStationNone(Section toAddSection) {
        if (!getStations().contains(toAddSection.getUpStation()) &&
                !getStations().contains(toAddSection.getDownStation())) {

            throw new RuntimeException();
        }
    }


    private Station getFirstStation() {
        List<Station> stations = getStations();
        return stations.get(0);
    }

    private Station getLastStation() {
        List<Station> stations = getStations();
        return stations.get(stations.size() - 1);
    }

    public void removeSectionByStationId(Long stationId) {
        if (sections.size() <= 1) {
            throw new RuntimeException();
        }

        boolean isNotValidUpStation = getLastStation().getId() != stationId;
        if (isNotValidUpStation) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }

        sections.stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> sections.remove(it));
    }
}
