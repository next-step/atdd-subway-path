package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    private Line() {
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

    public void addSection(Section newSection) {
        Optional<Section> addingSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst();

        addingSection.ifPresent(section -> section.updateUpStationToDownStationOf(newSection));

        sections.add(newSection);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(lastSection().getDownStation());

        return stations;
    }

    public void addSection2(Section newSection) {
        Optional<Section> addingSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst();

        addingSection.ifPresent(section -> section.updateUpStationToDownStationOf(newSection));

        sections.add(newSection);
    }

    public List<Station> getStations2() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Section firstSection = getFirstSection();
        stations.add(firstSection.getUpStation());

        boolean isLast = false;
        Station nextUpStation = firstSection.getDownStation();
        while (!isLast) {
            Section nextSection = getNextSectionOf(nextUpStation);
            if (nonNull(nextSection)) {
                stations.add(nextSection.getUpStation());
                nextUpStation = nextSection.getDownStation();
            } else {
                isLast = true;
            }
        }

        stations.add(nextUpStation);

        return stations;
    }

    private Section getFirstSection() {
        Section firstSection = sections.get(0);

        boolean isFirst = false;
        Station firstStation = firstSection.getUpStation();
        while (!isFirst) {
            Section frontSection = getFrontSectionOf(firstStation);
            if (nonNull(frontSection)) {
                firstStation = frontSection.getUpStation();
                firstSection = frontSection;
            } else {
                isFirst = true;
            }
        }
        return firstSection;
    }

    private Section getFrontSectionOf(Station frontStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(frontStation))
                .findFirst()
                .orElse(null);
    }

    private Section getNextSectionOf(Station nextUpStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(nextUpStation))
                .findFirst()
                .orElse(null);
    }

    public void removeSection(Station lastStation) {
        validateOnlyOneSection();
        validateIsLast(lastStation);
        sections.remove(sections.size() - 1);
    }

    private void validateOnlyOneSection() {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("한 개의 구간만이 존재합니다.");
        }
    }

    private void validateIsLast(Station lastStation) {
        if (!lastSection().getDownStation().equals(lastStation)) {
            throw new IllegalArgumentException("구간을 삭제할 수 없습니다.");
        }
    }

    private Section lastSection() {
        return sections.get(sections.size() - 1);
    }
}
