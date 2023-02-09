package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import nextstep.subway.exception.BothSectionStationsNotExistsInLineException;
import nextstep.subway.exception.SectionStationsAlreadyExistsInLineException;

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

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        if (section.hasIdenticalStations()) {
            throw new IllegalArgumentException();
        }

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();

        List<Station> stations = getStations();
        if (!stations.isEmpty()) {
            if (stations.contains(upStation) && stations.contains(downStation)) {
                throw new SectionStationsAlreadyExistsInLineException(upStation.getName(), downStation.getName());
            }

            if (!stations.contains(upStation) && !stations.contains(downStation)) {
                throw new BothSectionStationsNotExistsInLineException(upStation.getName(), downStation.getName());
            }
        }

        sections.stream()
            .filter(it -> it.getUpStation().equals(upStation))
            .findFirst()
            .ifPresent(it -> it.updateUpStation(downStation, distance));

        sections.stream()
            .filter(it -> it.getDownStation().equals(downStation))
            .findFirst()
            .ifPresent(it -> it.updateDownStation(upStation, distance));

        sections.add(section);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        Station firstStation = findFirstUpStation();

        List<Station> stations = new ArrayList<>();
        stations.add(firstStation);

        Optional<Section> preSection = sections.stream()
            .filter(it -> it.hasUpStation(firstStation))
            .findFirst();

        while (preSection.isPresent()) {
            Station downStation = preSection.get().getDownStation();
            stations.add(downStation);
            preSection = sections.stream()
                .filter(it -> it.hasUpStation(downStation))
                .findFirst();
        }

        return stations;
    }

    private Station findFirstUpStation() {
        List<Station> downStations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        return sections.stream()
            .map(Section::getUpStation)
            .filter(it -> !downStations.contains(it))
            .findFirst()
            .orElseThrow();
    }

    public void removeSection(Station station) {
        if (isSingleSection() || !isLastStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.size() - 1);
    }

    private boolean isSingleSection() {
        return sections.size() == 1;
    }

    private boolean isLastStation(Station station) {
        return getLastStation().equals(station);
    }

    public Station getLastStation() {
        return sections.get(sections.size() - 1).getDownStation();
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
}
